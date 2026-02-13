package br.com.codenest.marknow.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public final class UtilsString {

    private UtilsString() {
    }

    private static final Pattern ESPACOS = Pattern.compile("\\s+");
    private static final Pattern DIACRITICOS = Pattern.compile("\\p{M}+");
    private static final Pattern NAO_SLUG = Pattern.compile("[^a-z0-9\\-]+");
    private static final Pattern HIFENS_REPETIDOS = Pattern.compile("\\-+");

    /**
     * true se tiver texto após trim (não nulo, não vazio, não só espaços).
     */
    public static boolean temTexto(String v) {
        return v != null && !v.trim().isEmpty();
    }

    /**
     * trim e transforma vazio em null.
     */
    public static String apararParaNull(String v) {
        if (v == null) return null;
        String s = v.trim();
        return s.isEmpty() ? null : s;
    }

    /**
     * trim + colapsa espaços internos: " a   b " -> "a b"
     */
    public static String colapsarEspacosParaNull(String v) {
        String s = apararParaNull(v);
        if (s == null) return null;
        return ESPACOS.matcher(s).replaceAll(" ");
    }

    /**
     * trim + maiúsculo (Locale.ROOT).
     */
    public static String maiusculoParaNull(String v) {
        String s = apararParaNull(v);
        return s == null ? null : s.toUpperCase(Locale.ROOT);
    }

    /**
     * remove acentos (para busca/comparação, não para persistir “oficial”).
     */
    public static String removerAcentosParaNull(String v) {
        String s = apararParaNull(v);
        if (s == null) return null;

        String norm = Normalizer.normalize(s, Normalizer.Form.NFD);
        norm = DIACRITICOS.matcher(norm).replaceAll("");
        return norm.isEmpty() ? null : norm;
    }

    /**
     * chave padrão para comparação: trim -> colapsa espaços -> remove acentos -> upper
     */
    public static String chaveComparacaoParaNull(String v) {
        String s = colapsarEspacosParaNull(v);
        if (s == null) return null;
        s = removerAcentosParaNull(s);
        return s == null ? null : s.toUpperCase(Locale.ROOT);
    }

    /**
     * Slug seguro (URL-friendly): remove acentos, baixa, troca espaços por hífen,
     * remove caracteres inválidos e colapsa hífens.
     */
    public static String slugParaNull(String v) {
        String s = colapsarEspacosParaNull(v);
        if (s == null) return null;

        s = removerAcentosParaNull(s);
        if (s == null) return null;

        s = s.toLowerCase(Locale.ROOT);
        s = s.replace(" ", "-");
        s = NAO_SLUG.matcher(s).replaceAll("");
        s = HIFENS_REPETIDOS.matcher(s).replaceAll("-");
        s = trimHifens(s);

        return s.isEmpty() ? null : s;
    }

    private static String trimHifens(String s) {
        int start = 0;
        int end = s.length();
        while (start < end && s.charAt(start) == '-') start++;
        while (end > start && s.charAt(end - 1) == '-') end--;
        return s.substring(start, end);
    }

    /**
     * Substitui tokens por valores (sem regex). Ex.: "{nome}" -> "João".
     */
    public static String substituirTokens(String template, Map<String, String> tokens) {
        if (!temTexto(template) || tokens == null || tokens.isEmpty()) return template;

        String out = template;
        for (var e : tokens.entrySet()) {
            String key = e.getKey();
            if (key == null) continue;
            String val = e.getValue() == null ? "" : e.getValue();
            out = out.replace(key, val);
        }
        return out;
    }
}
