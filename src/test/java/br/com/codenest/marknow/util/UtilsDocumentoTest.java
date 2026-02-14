package br.com.codenest.marknow.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsDocumentoTest {

    // Fixtures válidas (mantém fixo no teste)
    private static final String CPF_VALIDO_DIGITOS = "52998224725";
    private static final String CPF_VALIDO_MASCARA = "529.982.247-25";

    private static final String CNPJ_VALIDO_DIGITOS = "04252011000110";
    private static final String CNPJ_VALIDO_MASCARA = "04.252.011/0001-10";

    @Test
    void apenasDigitosParaNull_removeMascaraETrim() {
        assertEquals("12345678901", UtilsDocumento.apenasDigitosParaNull(" 123.456.789-01 "));
    }

    @Test
    void apenasDigitosParaNull_nullEVazio() {
        assertNull(UtilsDocumento.apenasDigitosParaNull(null));
        assertNull(UtilsDocumento.apenasDigitosParaNull("   "));
        assertNull(UtilsDocumento.apenasDigitosParaNull("---"));
        assertNull(UtilsDocumento.apenasDigitosParaNull("\n\t"));
    }

    @Test
    void formatarCpfCnpj_formataQuandoTamanhoOk_senaoRetornaDigitos() {
        assertEquals("529.982.247-25", UtilsDocumento.formatarCpfCnpj(CPF_VALIDO_DIGITOS));
        assertEquals("04.252.011/0001-10", UtilsDocumento.formatarCpfCnpj(CNPJ_VALIDO_DIGITOS));

        assertEquals("123", UtilsDocumento.formatarCpfCnpj("123")); // tamanho não casa -> retorna dígitos
        assertNull(UtilsDocumento.formatarCpfCnpj(null));
    }

    @Test
    void formatarCep_formataSomenteQuando8Digitos() {
        assertEquals("25905-685", UtilsDocumento.formatarCep("25905685"));
        assertEquals("2590568", UtilsDocumento.formatarCep("2590568")); // 7 dígitos -> retorna como vier
        assertNull(UtilsDocumento.formatarCep("   "));
    }

    @Test
    void formatarTelefoneBr_formata10e11_demasRetornaDigitos() {
        assertEquals("(11) 3456-7890", UtilsDocumento.formatarTelefoneBr("1134567890"));
        assertEquals("(11) 93456-7890", UtilsDocumento.formatarTelefoneBr("11934567890"));

        assertEquals("1199", UtilsDocumento.formatarTelefoneBr("11 99")); // tamanho diferente -> retorna dígitos
        assertNull(UtilsDocumento.formatarTelefoneBr(null));
    }

    @Test
    void cpfValido_trueParaCpfValido_falseParaCasosComuns() {
        assertTrue(UtilsDocumento.cpfValido(CPF_VALIDO_DIGITOS));
        assertTrue(UtilsDocumento.cpfValido(CPF_VALIDO_MASCARA));

        assertFalse(UtilsDocumento.cpfValido(null));
        assertFalse(UtilsDocumento.cpfValido(""));                 // vazio
        assertFalse(UtilsDocumento.cpfValido("123"));              // curto
        assertFalse(UtilsDocumento.cpfValido("111.111.111-11"));   // repetidos
        assertFalse(UtilsDocumento.cpfValido("52998224724"));      // dígito errado
    }

    @Test
    void cnpjValido_trueParaCnpjValido_falseParaCasosComuns() {
        assertTrue(UtilsDocumento.cnpjValido(CNPJ_VALIDO_DIGITOS));
        assertTrue(UtilsDocumento.cnpjValido(CNPJ_VALIDO_MASCARA));

        assertFalse(UtilsDocumento.cnpjValido(null));
        assertFalse(UtilsDocumento.cnpjValido(""));                   // vazio
        assertFalse(UtilsDocumento.cnpjValido("123"));                // curto
        assertFalse(UtilsDocumento.cnpjValido("00.000.000/0000-00")); // repetidos
        assertFalse(UtilsDocumento.cnpjValido("04252011000111"));     // dígito errado
    }

    @Test
    void cpfOuCnpjValido_deveValidarPeloTamanho() {
        assertTrue(UtilsDocumento.cpfOuCnpjValido(CPF_VALIDO_DIGITOS));
        assertTrue(UtilsDocumento.cpfOuCnpjValido(CNPJ_VALIDO_DIGITOS));

        assertFalse(UtilsDocumento.cpfOuCnpjValido("123")); // nem 11 nem 14
        assertFalse(UtilsDocumento.cpfOuCnpjValido("11111111111")); // inválido
        assertFalse(UtilsDocumento.cpfOuCnpjValido("00000000000000")); // inválido
    }

    @Test
    void pessoaJuridica_trueApenasPara14Digitos() {
        assertTrue(UtilsDocumento.pessoaJuridica(CNPJ_VALIDO_DIGITOS));
        assertTrue(UtilsDocumento.pessoaJuridica(CNPJ_VALIDO_MASCARA));

        assertFalse(UtilsDocumento.pessoaJuridica(CPF_VALIDO_DIGITOS));
        assertFalse(UtilsDocumento.pessoaJuridica(null));
        assertFalse(UtilsDocumento.pessoaJuridica("123"));
    }

    @Test
    void raizCnpj_soRetornaParaCnpjValido() {
        assertEquals("04252011", UtilsDocumento.raizCnpj(CNPJ_VALIDO_DIGITOS));
        assertEquals("04252011", UtilsDocumento.raizCnpj(CNPJ_VALIDO_MASCARA));

        assertNull(UtilsDocumento.raizCnpj("04252011000111")); // inválido
        assertNull(UtilsDocumento.raizCnpj("123"));            // curto
        assertNull(UtilsDocumento.raizCnpj(null));
    }

    @Test
    void mesmaRaizCnpj_trueQuandoRaizesIguais() {
        String cnpj1 = "04.252.011/0001-10";
        String cnpj2 = gerarCnpjValidoComRaiz("04252011"); // raiz do primeiro

        assertTrue(UtilsDocumento.cnpjValido(cnpj2));
        assertTrue(UtilsDocumento.mesmaRaizCnpj(cnpj1, cnpj2));
    }

    @Test
    void numeroAleatorio_invariantes() {
        assertEquals("", UtilsDocumento.numeroAleatorio(0));
        assertEquals("", UtilsDocumento.numeroAleatorio(-1));

        String n = UtilsDocumento.numeroAleatorio(10);
        assertEquals(10, n.length());
        assertTrue(n.matches("\\d+"));
        assertNotEquals('0', n.charAt(0)); // primeiro não pode ser 0
    }


    private static String gerarCnpjValidoComRaiz(String raiz8) {
        // raiz8: 8 dígitos, ex "04252011"
        String base = raiz8 + "0001"; // 12 dígitos (raiz + filial)
        int d1 = calcularDigitoTeste(base, new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        int d2 = calcularDigitoTeste(base + d1, new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        return base + d1 + d2;
    }

    private static int calcularDigitoTeste(String str, int[] peso) {
        int soma = 0;
        for (int i = 0; i < str.length(); i++) soma += (str.charAt(i) - '0') * peso[i];
        int mod = soma % 11;
        return (mod < 2) ? 0 : (11 - mod);
    }
}
