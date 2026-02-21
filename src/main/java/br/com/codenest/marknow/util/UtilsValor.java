package br.com.codenest.marknow.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class UtilsValor {

    private UtilsValor() {
    }

    /**
     * Garante que um Integer nunca seja null (retorna 0 se for).
     */
    public static int padraoZero(Integer valor) {
        return valor == null ? 0 : valor;
    }

    /**
     * Garante que um BigDecimal nunca seja null (retorna ZERO se for).
     */
    public static BigDecimal padraoZero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    /**
     * Compara se o valor é maior que zero. Útil para validar preços ou cotas.
     */
    public static boolean isMaiorQueZero(BigDecimal valor) {
        return valor != null && valor.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Converte String para BigDecimal com segurança.
     */
    public static BigDecimal paraBigDecimal(String valor) {
        if (!UtilsString.temTexto(valor)) return BigDecimal.ZERO;
        try {
            // Remove possíveis máscaras de moeda se necessário antes do parse
            String limpo = valor.replace(".", "").replace(",", ".");
            return new BigDecimal(limpo).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Verifica se um número está entre um intervalo (inclusive).
     */
    public static boolean isNoIntervalo(Integer valor, int min, int max) {
        return valor != null && valor >= min && valor <= max;
    }

    /**
     * Calcula a porcentagem de um valor sobre o total (ex: para o alerta de 80% das cotas).
     */
    public static double calcularPercentual(long atual, int limite) {
        if (limite <= 0) return 0.0;
        return ((double) atual / limite) * 100;
    }
}