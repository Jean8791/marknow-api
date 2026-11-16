package br.com.codenest.marknow.service.agenda;

import io.agroal.api.AgroalDataSource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Exercita a constraint ex_agendamento_sem_sobreposicao (V11__agenda.sql) contra o PostgreSQL real
 * do profile de teste (database_test, porta 5433), sem mock. Os dados são criados pela própria API.
 */
@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AgendamentoConcorrenciaTest {

    private static final String SQLSTATE_EXCLUSAO = "23P01";
    private static final int REQUISICOES_SIMULTANEAS = 8;
    private static final String SENHA_DE_TESTE = "senha-de-teste-123";

    // Segunda-feira distante, para nunca cair no passado nem fora da disponibilidade (seg 08:00–18:00).
    private static final LocalDate SEGUNDA = LocalDate.now(AgendaRegras.ZONA)
            .plusMonths(2).with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));

    @Inject
    AgroalDataSource dataSource;

    private String token;
    private String oidServico;

    // @BeforeEach (e não @BeforeAll): só aqui o RestAssured já aponta para a porta aleatória do teste
    @BeforeEach
    void prepararPrestadorEServico() throws SQLException {
        if (token != null) return;
        String oidPlano = UUID.randomUUID().toString();
        String codigoConvite = "CONC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        executar("insert into plano (oid, criado_em, nome, limite_agendamentos, valor, status, possui_trial, dias_trial) "
                + "values (?, now(), 'Plano de teste', 1000, 0, 'ATIVO', false, 0)", oidPlano);
        executar("insert into convite (oid, criado_em, codigo, expira_em, max_usos, qtd_usado) "
                + "values (?, now(), ?, now() + interval '1 day', 1, 0)", UUID.randomUUID().toString(), codigoConvite);

        token = given().contentType(ContentType.JSON)
                .body(Map.of(
                        "codigo", codigoConvite,
                        "oidPlano", oidPlano,
                        "prestador", Map.of(
                                "nome", "Prestador de Teste", "tipoPessoa", "PF", "email", "prestador@exemplo.test",
                                "dddTelefone", "11", "cep", "01001000", "logradouro", "Rua Teste",
                                "numero", "1", "bairro", "Centro", "cidade", "Sao Paulo", "uf", "SP"),
                        "usuario", Map.of(
                                "nome", "Admin de Teste", "email", "admin@exemplo.test", "tipoPessoa", "PF",
                                "dddTelefone", "11", "senha", SENHA_DE_TESTE, "comfirmarSenha", SENHA_DE_TESTE)))
                .post("/auth/cadastro")
                .then().statusCode(201)
                .extract().path("token");

        oidServico = criarServico();
    }

    @Test
    void deveCriarSomenteUmAgendamentoQuandoVariasRequisicoesDisputamOMesmoHorario() throws Exception {
        String profissional = criarProfissional();
        LocalDateTime inicio = SEGUNDA.atTime(10, 0);

        List<Integer> status = dispararSimultaneamente(REQUISICOES_SIMULTANEAS,
                i -> () -> postAgendamento(profissional, inicio, "Cliente " + i).statusCode());

        assertEquals(1, status.stream().filter(s -> s == 201).count(), "criados: " + status);
        assertEquals(REQUISICOES_SIMULTANEAS - 1, status.stream().filter(s -> s == 409).count(), "conflitos: " + status);
        assertEquals(1, contarAgendados(profissional));
    }

    @Test
    void deveLiberarOHorarioQuandoOAgendamentoForCancelado() {
        String profissional = criarProfissional();
        LocalDateTime inicio = SEGUNDA.atTime(10, 0);

        String oid = postAgendamento(profissional, inicio, "Primeiro").then().statusCode(201).extract().path("oid");
        postAgendamento(profissional, inicio, "Segundo").then().statusCode(409);

        given().auth().oauth2(token).contentType(ContentType.JSON).post("/agendamento/{oid}/cancelar", oid).then().statusCode(200);

        postAgendamento(profissional, inicio, "Terceiro").then().statusCode(201);
    }

    @Test
    void naoDeveConflitarAgendamentosQueSoSeTocamNoFimDeUmENoInicioDoOutro() {
        String profissional = criarProfissional();

        postAgendamento(profissional, SEGUNDA.atTime(10, 0), "Das 10:00 às 10:30").then().statusCode(201);
        postAgendamento(profissional, SEGUNDA.atTime(10, 30), "Das 10:30 às 11:00").then().statusCode(201);

        assertEquals(2, contarAgendados(profissional));
    }

    @Test
    void deveRecusarNoBancoUmIntervaloSobrepostoMesmoSemPassarPelaChecagemDoServico() throws SQLException {
        String profissional = criarProfissional();
        Instant inicio = SEGUNDA.atTime(10, 0).atZone(AgendaRegras.ZONA).toInstant();
        inserirAgendamento(profissional, inicio, inicio.plus(30, ChronoUnit.MINUTES), "AGENDADO");

        SQLException erro = assertThrows(SQLException.class, () ->
                inserirAgendamento(profissional, inicio.plus(15, ChronoUnit.MINUTES), inicio.plus(45, ChronoUnit.MINUTES), "AGENDADO"));
        assertEquals(SQLSTATE_EXCLUSAO, erro.getSQLState());

        // a constraint só vale para AGENDADO: um cancelado no mesmo intervalo é aceito
        inserirAgendamento(profissional, inicio, inicio.plus(30, ChronoUnit.MINUTES), "CANCELADO");
    }

    private <T> List<T> dispararSimultaneamente(int quantidade, java.util.function.IntFunction<Callable<T>> tarefa) throws Exception {
        CyclicBarrier largada = new CyclicBarrier(quantidade);
        ExecutorService pool = Executors.newFixedThreadPool(quantidade);
        try {
            List<Future<T>> futuros = new ArrayList<>();
            for (int i = 0; i < quantidade; i++) {
                Callable<T> corpo = tarefa.apply(i);
                futuros.add(pool.submit(() -> {
                    largada.await();
                    return corpo.call();
                }));
            }
            List<T> resultados = new ArrayList<>();
            for (Future<T> futuro : futuros) resultados.add(futuro.get());
            return resultados;
        } finally {
            pool.shutdownNow();
        }
    }

    private Response postAgendamento(String oidProfissional, LocalDateTime inicio, String cliente) {
        return given().auth().oauth2(token).contentType(ContentType.JSON)
                .body(Map.of(
                        "oidProfissional", oidProfissional,
                        "oidServico", oidServico,
                        "inicio", inicio.toString(),
                        "clienteNome", cliente,
                        "clienteDdd", "11",
                        "clienteTelefone", "999990000"))
                .post("/agendamento");
    }

    private String criarProfissional() {
        return given().auth().oauth2(token).contentType(ContentType.JSON)
                .body(Map.of(
                        "nome", "Profissional " + UUID.randomUUID().toString().substring(0, 8),
                        "disponibilidades", List.of(Map.of("diaSemana", 1, "horaInicio", "08:00", "horaFim", "18:00"))))
                .post("/profissional")
                .then().statusCode(201)
                .extract().path("oid");
    }

    private String criarServico() {
        return given().auth().oauth2(token).contentType(ContentType.JSON)
                .body(Map.of("nome", "Serviço de 30 minutos", "duracaoMinutos", 30))
                .post("/servico")
                .then().statusCode(201)
                .extract().path("oid");
    }

    private long contarAgendados(String oidProfissional) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "select count(*) from agendamento where oid_profissional = ? and status = 'AGENDADO'")) {
            ps.setString(1, oidProfissional);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private void inserirAgendamento(String oidProfissional, Instant inicio, Instant fim, String status) throws SQLException {
        executar("insert into agendamento (oid, criado_em, oid_prestador, criado_por, oid_profissional, oid_servico, "
                        + "cliente_nome, cliente_ddd, cliente_telefone, inicio, fim, status) "
                        + "select ?, now(), s.oid_prestador, 'teste', ?, s.oid, 'Cliente direto', '11', '999990000', ?, ?, ? "
                        + "from servico s where s.oid = ?",
                UUID.randomUUID().toString(), oidProfissional, Timestamp.from(inicio), Timestamp.from(fim), status, oidServico);
    }

    private void executar(String sql, Object... parametros) throws SQLException {
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) ps.setObject(i + 1, parametros[i]);
            ps.executeUpdate();
        }
    }
}
