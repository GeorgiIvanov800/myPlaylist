package org.myplaylist.myplaylist.model.validation.anotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.myplaylist.myplaylist.model.validation.validators.UniqueUserEmailValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = UniqueUserEmailValidator.class)
public @interface UniqueUserEmail {
    String message() default "Email is already in use";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
