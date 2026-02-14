package br.com.codenest.marknow.util;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UtilsStringTest {

    @Test
    void temTexto() {
        assertFalse(UtilsString.temTexto(null));
        assertFalse(UtilsString.temTexto(""));
        assertFalse(UtilsString.temTexto("   "));
        assertTrue(UtilsString.temTexto(" a "));
    }

    @Test
    void apararParaNull() {
        assertNull(UtilsString.apararParaNull(null));
        assertNull(UtilsString.apararParaNull(""));
        assertNull(UtilsString.apararParaNull("   "));
        assertEquals("abc", UtilsString.apararParaNull("  abc  "));
    }

    @Test
    void colapsarEspacosParaNull() {
        assertNull(UtilsString.colapsarEspacosParaNull(null));
        assertNull(UtilsString.colapsarEspacosParaNull("   "));
        assertEquals("a b", UtilsString.colapsarEspacosParaNull("  a   b  "));
        assertEquals("a b c", UtilsString.colapsarEspacosParaNull("a\tb\nc"));
    }

    @Test
    void maiusculoParaNull() {
        assertNull(UtilsString.maiusculoParaNull(null));
        assertNull(UtilsString.maiusculoParaNull("   "));
        assertEquals("ABC", UtilsString.maiusculoParaNull(" abc "));
    }

    @Test
    void removerAcentosParaNull() {
        assertNull(UtilsString.removerAcentosParaNull(null));
        assertNull(UtilsString.removerAcentosParaNull("   "));
        assertEquals("Joao", UtilsString.removerAcentosParaNull("João"));
        assertEquals("AEIOU c", UtilsString.removerAcentosParaNull("ÁÉÍÓÚ ç"));
    }

    @Test
    void chaveComparacaoParaNull() {
        assertNull(UtilsString.chaveComparacaoParaNull(null));
        assertNull(UtilsString.chaveComparacaoParaNull("   "));
        assertEquals("JOAO DA SILVA", UtilsString.chaveComparacaoParaNull("  João   da  Silva "));
    }

    @Test
    void slugParaNull_basico() {
        assertNull(UtilsString.slugParaNull(null));
        assertNull(UtilsString.slugParaNull("   "));
        assertEquals("joao-da-silva", UtilsString.slugParaNull("João da Silva"));
    }

    @Test
    void slugParaNull_removeCaracteresInvalidos_colapsaHifens_eTrima() {
        assertEquals("acao-reacao", UtilsString.slugParaNull("Ação & Reação!"));
        assertEquals("abc", UtilsString.slugParaNull("---abc---"));
        assertEquals("a-b", UtilsString.slugParaNull("a---b"));
        assertNull(UtilsString.slugParaNull("!!!")); // vira vazio -> null
    }

    @Test
    void substituirTokens_retornaOriginalQuandoNaoAplicavel() {
        assertNull(UtilsString.substituirTokens(null, Map.of("{x}", "1")));
        assertEquals("", UtilsString.substituirTokens("", Map.of("{x}", "1")));
        assertEquals("   ", UtilsString.substituirTokens("   ", Map.of("{x}", "1")));
        assertEquals("oi {nome}", UtilsString.substituirTokens("oi {nome}", Map.of()));
        assertEquals("oi {nome}", UtilsString.substituirTokens("oi {nome}", null));
    }

    @Test
    void substituirTokens_substituiTudo_semRegex() {
        Map<String, String> tokens = Map.of(
                "{nome}", "João",
                "{empresa}", "MarkNow"
        );

        String out = UtilsString.substituirTokens("Oi {nome}, bem-vindo à {empresa}!", tokens);
        assertEquals("Oi João, bem-vindo à MarkNow!", out);
    }

    @Test
    void substituirTokens_valorNullViraVazio() {
        Map<String, String> tokens = new LinkedHashMap<>();
        tokens.put("{x}", null);

        assertEquals("a  b", UtilsString.substituirTokens("a {x} b", tokens));
    }

    @Test
    void substituirTokens_repeticaoDoToken() {
        Map<String, String> tokens = Map.of("{n}", "X");
        assertEquals("X-X-X", UtilsString.substituirTokens("{n}-{n}-{n}", tokens));
    }

    @Test
    void substituirTokens_colisaoDeChaves_ordemImporta_seVoceUsarLinkedHashMap() {
        // Esse teste é pra te lembrar do risco, não pra “provar perfeição”.
        // Com LinkedHashMap você controla a ordem.
        Map<String, String> tokens = new LinkedHashMap<>();
        tokens.put("{ab}", "AB");
        tokens.put("{a}", "A");

        assertEquals("AB A", UtilsString.substituirTokens("{ab} {a}", tokens));
    }
}
