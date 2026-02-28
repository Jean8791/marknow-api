package br.com.codenest.marknow.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = TelefoneBrasilValidator.class)
public @interface TelefoneBrasil {
    String message() default "Número de telefone ou celular inválido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}