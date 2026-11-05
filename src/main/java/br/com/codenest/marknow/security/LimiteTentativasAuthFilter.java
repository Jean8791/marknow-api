package br.com.codenest.marknow.security;

import io.vertx.core.http.HttpServerRequest;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Provider
@Priority(Priorities.AUTHENTICATION - 100)
public class LimiteTentativasAuthFilter implements ContainerRequestFilter {

    private static final int LIMPEZA_ACIMA_DE = 10_000;

    private final Map<String, Janela> janelas = new ConcurrentHashMap<>();

    @Context
    HttpServerRequest request;

    @ConfigProperty(name = "marknow.auth.limite-tentativas", defaultValue = "10")
    int limite;

    @ConfigProperty(name = "marknow.auth.janela-minutos", defaultValue = "15")
    int janelaMinutos;

    @ConfigProperty(name = "marknow.auth.cabecalho-ip")
    Optional<String> cabecalhoIp;

    @Override
    public void filter(ContainerRequestContext ctx) {
        if (!"POST".equals(ctx.getMethod())) return;
        String path = ctx.getUriInfo().getPath();
        if (!path.startsWith("/auth")) return;

        Instant agora = Instant.now();
        Duration duracao = Duration.ofMinutes(janelaMinutos);
        if (janelas.size() > LIMPEZA_ACIMA_DE) {
            janelas.values().removeIf(j -> j.expirou(agora));
        }

        String chave = ipCliente(ctx) + "|" + path;
        Janela janela = janelas.compute(chave, (k, atual) ->
                atual == null || atual.expirou(agora) ? new Janela(agora.plus(duracao), 1) : atual.incrementar());

        if (janela.tentativas() > limite) {
            long segundos = Math.max(1, Duration.between(agora, janela.expiraEm()).toSeconds());
            ctx.abortWith(Response.status(Response.Status.TOO_MANY_REQUESTS)
                    .header("Retry-After", segundos)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new TenantAssinaturaFilter.Error("Muitas tentativas. Tente novamente em alguns minutos."))
                    .build());
        }
    }

    private String ipCliente(ContainerRequestContext ctx) {
        if (cabecalhoIp.isPresent()) {
            String valor = ctx.getHeaderString(cabecalhoIp.get());
            if (valor != null && !valor.isBlank()) {
                return valor.split(",")[0].trim();
            }
        }
        return request != null && request.remoteAddress() != null ? request.remoteAddress().hostAddress() : "desconhecido";
    }

    private record Janela(Instant expiraEm, int tentativas) {
        boolean expirou(Instant agora) {
            return !agora.isBefore(expiraEm);
        }

        Janela incrementar() {
            return new Janela(expiraEm, tentativas + 1);
        }
    }
}
