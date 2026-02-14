package br.com.codenest.marknow.util;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UtilsRandomTest {

    @Test
    void uuid_deveSerUuidValido() {
        String u = UtilsRandom.uuid();
        assertNotNull(u);
        assertDoesNotThrow(() -> UUID.fromString(u));
        assertEquals(36, u.length()); // formato padrão com hífens
    }

    @Test
    void numeroDigitos_quandoTamanhoInvalido_retornaVazio() {
        assertEquals("", UtilsRandom.numeroDigitos(0));
        assertEquals("", UtilsRandom.numeroDigitos(-1));
    }

    @Test
    void numeroDigitos_deveTerApenasDigitos_tamanhoCerto_primeiroNaoZero() {
        String n = UtilsRandom.numeroDigitos(10);

        assertEquals(10, n.length());
        assertTrue(n.matches("\\d+"));
        assertNotEquals('0', n.charAt(0));
    }

    @Test
    void numeroDigitos_tamanho1_primeiroNaoZero() {
        String n = UtilsRandom.numeroDigitos(1);
        assertEquals(1, n.length());
        assertTrue(n.matches("[1-9]"));
    }

    @Test
    void hex_quandoBytesInvalido_retornaVazio() {
        assertEquals("", UtilsRandom.hex(0));
        assertEquals("", UtilsRandom.hex(-10));
    }

    @Test
    void hex_deveTerTamanhoBytesVezes2_eApenasHexLowercase() {
        String h = UtilsRandom.hex(16); // 16 bytes => 32 chars
        assertEquals(32, h.length());
        assertTrue(h.matches("[0-9a-f]+"));
    }

    @Test
    void hex_bytes1_deveTer2Chars() {
        String h = UtilsRandom.hex(1);
        assertEquals(2, h.length());
        assertTrue(h.matches("[0-9a-f]{2}"));
    }

    @Test
    void hex_naoDeveRetornarSempreIgual() {
        // Teste fraco mas útil: chance de colisão aqui é absurdamente baixa.
        String h1 = UtilsRandom.hex(16);
        String h2 = UtilsRandom.hex(16);
        assertNotEquals(h1, h2);
    }

    @Test
    void numeroDigitos_naoDeveRetornarSempreIgual() {
        String a = UtilsRandom.numeroDigitos(12);
        String b = UtilsRandom.numeroDigitos(12);
        assertNotEquals(a, b);
    }
}
