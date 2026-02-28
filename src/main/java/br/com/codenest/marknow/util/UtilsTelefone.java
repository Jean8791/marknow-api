package br.com.codenest.marknow.util;

import java.util.regex.Pattern;

public class UtilsTelefone {

    // Regex para validar apenas números
    private static final Pattern ONLY_NUMBERS = Pattern.compile("\\D");

    /**
     * Remove parênteses, espaços e traços, deixando apenas os números.
     */
    public static String apenasNumeros(String telefone) {
        if (telefone == null) return null;
        return ONLY_NUMBERS.matcher(telefone).replaceAll("");
    }

    /**
     * Valida se o DDD tem 2 dígitos.
     */
    public static boolean isDddValido(String ddd) {
        String cleanDdd = apenasNumeros(ddd);
        return cleanDdd != null && cleanDdd.length() == 2;
    }

    /**
     * Valida se o Telefone Fixo tem 8 dígitos.
     */
    public static boolean isTelefoneFixoValido(String telefone) {
        String clean = apenasNumeros(telefone);
        return clean != null && clean.length() == 8;
    }

    /**
     * Valida se o Celular tem 9 dígitos (padrão brasileiro atual).
     */
    public static boolean isCelularValido(String celular) {
        String clean = apenasNumeros(celular);
        return clean != null && clean.length() == 9;
    }

    /**
     * Formata para exibição amigável: (XX) XXXXX-XXXX ou (XX) XXXX-XXXX
     */
    public static String formatarCompleto(String ddd, String numero) {
        if (ddd == null || numero == null) return null;
        String cleanDdd = apenasNumeros(ddd);
        String cleanNum = apenasNumeros(numero);

        if (cleanNum.length() == 9) {
            return String.format("(%s) %s-%s", cleanDdd, cleanNum.substring(0, 5), cleanNum.substring(5));
        } else if (cleanNum.length() == 8) {
            return String.format("(%s) %s-%s", cleanDdd, cleanNum.substring(0, 4), cleanNum.substring(4));
        }
        return ddd + " " + numero;
    }
}