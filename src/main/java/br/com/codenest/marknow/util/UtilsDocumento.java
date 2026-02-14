package br.com.codenest.marknow.util;

import java.security.SecureRandom;
import java.util.regex.Pattern;

public final class UtilsDocumento {

    private UtilsDocumento() {
    }

    private static final Pattern NAO_DIGITOS = Pattern.compile("\\D+");

    public static String apenasDigitosParaNull(String v) {
        if (v == null) return null;
        String s = v.trim();
        if (s.isEmpty()) return null;
        s = NAO_DIGITOS.matcher(s).replaceAll("");
        return s.isEmpty() ? null : s;
    }

    public static String formatarCpfCnpj(String v) {
        String d = apenasDigitosParaNull(v);
        if (d == null) return null;
        if (d.length() == 11) return aplicarMascara("###.###.###-##", d);
        if (d.length() == 14) return aplicarMascara("##.###.###/####-##", d);
        return d;
    }

    public static String formatarCep(String v) {
        String d = apenasDigitosParaNull(v);
        if (d == null) return null;
        if (d.length() != 8) return d;
        return d.substring(0, 5) + "-" + d.substring(5);
    }

    public static String formatarTelefoneBr(String v) {
        String d = apenasDigitosParaNull(v);
        if (d == null) return null;

        if (d.length() == 10) return "(" + d.substring(0, 2) + ") " + d.substring(2, 6) + "-" + d.substring(6);
        if (d.length() == 11) return "(" + d.substring(0, 2) + ") " + d.substring(2, 7) + "-" + d.substring(7);

        return d;
    }

    public static boolean cpfValido(String v) {
        String cpf = apenasDigitosParaNull(v);
        if (cpf == null || cpf.length() != 11) return false;
        if (todosIguais(cpf)) return false;

        int d1 = calcularDigito(cpf.substring(0, 9), new int[]{10, 9, 8, 7, 6, 5, 4, 3, 2});
        int d2 = calcularDigito(cpf.substring(0, 9) + d1, new int[]{11, 10, 9, 8, 7, 6, 5, 4, 3, 2});
        return cpf.equals(cpf.substring(0, 9) + d1 + d2);
    }

    public static boolean cnpjValido(String v) {
        String cnpj = apenasDigitosParaNull(v);
        if (cnpj == null || cnpj.length() != 14) return false;
        if (todosIguais(cnpj)) return false;

        int d1 = calcularDigito(cnpj.substring(0, 12), new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        int d2 = calcularDigito(cnpj.substring(0, 12) + d1, new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        return cnpj.equals(cnpj.substring(0, 12) + d1 + d2);
    }

    public static boolean cpfOuCnpjValido(String v) {
        String d = apenasDigitosParaNull(v);
        if (d == null) return false;
        return d.length() == 11 ? cpfValido(d) : d.length() == 14 && cnpjValido(d);
    }

    public static boolean pessoaJuridica(String v) {
        String d = apenasDigitosParaNull(v);
        return d != null && d.length() == 14;
    }

    public static String raizCnpj(String cnpj) {
        String d = apenasDigitosParaNull(cnpj);
        if (d == null || d.length() != 14 || !cnpjValido(d)) return null;
        return d.substring(0, 8);
    }

    public static boolean mesmaRaizCnpj(String cnpj1, String cnpj2) {
        String r1 = raizCnpj(cnpj1);
        String r2 = raizCnpj(cnpj2);
        return r1 != null && r1.equals(r2);
    }

    public static String numeroAleatorio(int size) {
        if (size <= 0) return "";
        SecureRandom r = new SecureRandom();
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            int digit = (i == 0) ? (r.nextInt(9) + 1) : r.nextInt(10);
            sb.append(digit);
        }
        return sb.toString();
    }

    private static boolean todosIguais(String s) {
        char c = s.charAt(0);
        for (int i = 1; i < s.length(); i++) if (s.charAt(i) != c) return false;
        return true;
    }

    private static int calcularDigito(String str, int[] peso) {
        int soma = 0;
        for (int i = 0; i < str.length(); i++) {
            soma += (str.charAt(i) - '0') * peso[i];
        }
        int mod = soma % 11;
        return (mod < 2) ? 0 : (11 - mod);
    }

    private static String aplicarMascara(String mask, String digits) {
        StringBuilder sb = new StringBuilder(mask.length());
        int di = 0;
        for (int i = 0; i < mask.length(); i++) {
            char m = mask.charAt(i);
            if (m == '#') {
                if (di >= digits.length()) break;
                sb.append(digits.charAt(di++));
            } else {
                sb.append(m);
            }
        }
        return sb.toString();
    }
}
