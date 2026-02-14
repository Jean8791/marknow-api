package br.com.codenest.marknow.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = EmailStrictValidator.class)
public @interface EmailStrict {
    String message() default "email inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
