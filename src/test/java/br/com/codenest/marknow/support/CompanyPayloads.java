package br.com.codenest.marknow.support;

public final class CompanyPayloads {

    private CompanyPayloads() {
    }

    public static String create(String name, String country, String idType, String idValue) {
        return """
                {
                  "name": "%s",
                  "legalCountry": "%s",
                  "legalIdType": "%s",
                  "legalIdValue": "%s"
                }
                """.formatted(escape(name), escape(country), escape(idType), escape(idValue));
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}