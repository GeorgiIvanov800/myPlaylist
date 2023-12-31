package org.myplaylist.myplaylist.model.validation.anotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.myplaylist.myplaylist.model.validation.validators.FieldMatchValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = FieldMatchValidator.class)
public @interface FieldMatch {


    String message() default "Fields should match";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    String first();

    String second();
}
