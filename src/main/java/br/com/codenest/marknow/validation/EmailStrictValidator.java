package br.com.codenest.marknow.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailStrictValidator implements ConstraintValidator<EmailStrict, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true; // @NotBlank decide

        String v = value.trim();
        if (v.contains(" ")) return false;

        int at = v.indexOf('@');
        if (at <= 0) return false;                 // nada antes do @
        if (at != v.lastIndexOf('@')) return false; // só 1 @

        String local = v.substring(0, at);
        String domain = v.substring(at + 1);
        if (local.isBlank() || domain.isBlank()) return false;

        // domínio precisa ter pelo menos um ponto e não pode começar/terminar com ponto
        if (domain.startsWith(".") || domain.endsWith(".")) return false;
        if (!domain.contains(".")) return false;

        // sem pontos duplos no domínio
        if (domain.contains("..")) return false;

        return true;
    }
}
