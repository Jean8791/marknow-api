package br.com.codenest.marknow.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = CpfOrCnpjValidator.class)
public @interface CpfOrCnpj {
    String message() default "documento deve ser um CPF ou CNPJ válido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
