package br.com.codenest.marknow.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefoneBrasilValidator implements ConstraintValidator<TelefoneBrasil, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true; // Deixe @NotBlank decidir

        // Remove tudo que não for número
        String d = value.replaceAll("\\D+", "");

        // Valida se é um telefone fixo (8 dígitos) ou celular (9 dígitos)
        // Opcional: Você pode adicionar validação de prefixos proibidos aqui
        if (d.length() == 8 || d.length() == 9) {
            return !isSequenciaRepetida(d);
        }

        return false;
    }

    private boolean isSequenciaRepetida(String d) {
        // Evita números como 00000000 ou 999999999
        return d.chars().distinct().count() == 1;
    }
}