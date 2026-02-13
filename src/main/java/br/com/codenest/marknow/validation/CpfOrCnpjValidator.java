package br.com.codenest.marknow.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfOrCnpjValidator implements ConstraintValidator<CpfOrCnpj, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true; // deixe @NotBlank decidir se é obrigatório

        String d = value.replaceAll("\\D+", "");
        if (d.length() == 11) return isCpfValid(d);
        if (d.length() == 14) return isCnpjValid(d);
        return false;
    }

    private boolean isCpfValid(String cpf) {
        if (cpf.chars().distinct().count() == 1) return false; // evita 00000000000 etc.

        int d1 = calcCpfDigit(cpf, 10);
        int d2 = calcCpfDigit(cpf, 11);
        return (cpf.charAt(9) - '0') == d1 && (cpf.charAt(10) - '0') == d2;
    }

    private int calcCpfDigit(String cpf, int weightStart) {
        int sum = 0;
        for (int i = 0; i < weightStart - 1; i++) {
            sum += (cpf.charAt(i) - '0') * (weightStart - i);
        }
        int mod = (sum * 10) % 11;
        return (mod == 10) ? 0 : mod;
    }

    private boolean isCnpjValid(String cnpj) {
        if (cnpj.chars().distinct().count() == 1) return false;

        int d1 = calcCnpjDigit(cnpj, new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        int d2 = calcCnpjDigit(cnpj, new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});

        return (cnpj.charAt(12) - '0') == d1 && (cnpj.charAt(13) - '0') == d2;
    }

    private int calcCnpjDigit(String cnpj, int[] weights) {
        int sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += (cnpj.charAt(i) - '0') * weights[i];
        }
        int mod = sum % 11;
        return (mod < 2) ? 0 : 11 - mod;
    }
}
