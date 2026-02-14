package br.com.codenest.marknow.cadastro;


import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class CadastroResourceTest {

    @Test
    void deve_cadastrar_prestador_usuario_assinatura_e_retornar_token() {

        String body = """
                    {
                      "prestador": {
                        "nome": "Empresa Teste LTDA",
                        "documento": "12345678000199",
                        "tipoPessoa": "PJ"
                      },
                      "usuario": {
                        "nome": "Admin Teste",
                        "email": "admin@empresa.com",
                        "senha": "123456"
                      },
                      "plano": "BASICO",
                      "diasTrial": 14
                    }
                """;

        // 1) chama cadastro e captura oid/token
        var resp = given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/cadastro")
                .then()
                .statusCode(201)
                .body("oid", notNullValue())
                .body("token", notNullValue())
                .extract();

        String prestadorOid = resp.path("oid");
        String token = resp.path("token");

        // 2) chama um endpoint protegido qualquer
        // Ajuste a rota abaixo para alguma rota protegida que exista no seu projeto.
        // Sugestão: GET /prestador/{oid}
        given()
                .header("Authorization", "Bearer " + token)
                .header("X-Prestador-Oid", prestadorOid)
                .when()
                .get("/prestador/" + prestadorOid)
                .then()
                .statusCode(anyOf(is(200), is(404)));
        // Se você ainda não implementou GET prestador, pode dar 404.
        // O importante é NÃO dar 401/403 (token e filtro passaram).
    }

    @Test
    void nao_deve_cadastrar_com_email_duplicado() {

        String body = """
                    {
                      "prestador": { "nome": "Empresa 1", "documento": "12345678000199", "tipoPessoa": "PJ" },
                      "usuario": { "nome": "Admin", "email": "dup@empresa.com", "senha": "123456" },
                      "plano": "BASICO",
                      "diasTrial": 7
                    }
                """;

        given().contentType("application/json").body(body).post("/cadastro").then().statusCode(201);

        // segundo cadastro com mesmo email deve falhar
        given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/cadastro")
                .then()
                .statusCode(anyOf(is(400), is(409))); // depende de como você tratou a exceção
    }
}
