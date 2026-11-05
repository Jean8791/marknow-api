package br.com.codenest.marknow.util;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaginationUtilTest {

    private static final Set<String> PERMITIDOS = Set.of("nome", "criadoEm");

    @Test
    void campoOrdenacao_quandoPermitido_retornaOCampo() {
        assertEquals("nome", PaginationUtil.campoOrdenacao("nome", PERMITIDOS, "criadoEm"));
    }

    @Test
    void campoOrdenacao_quandoNaoPermitido_retornaPadrao() {
        assertEquals("criadoEm", PaginationUtil.campoOrdenacao("nome, (select 1)", PERMITIDOS, "criadoEm"));
        assertEquals("criadoEm", PaginationUtil.campoOrdenacao("senha", PERMITIDOS, "criadoEm"));
    }

    @Test
    void campoOrdenacao_quandoNulo_retornaPadrao() {
        assertEquals("criadoEm", PaginationUtil.campoOrdenacao(null, PERMITIDOS, "criadoEm"));
    }
}
