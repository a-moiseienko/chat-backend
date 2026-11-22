package com.example.chat.api;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Limits text by Unicode code points, so one emoji counts as one character.
 * {@code @Size} counts UTF-16 code units and would count an emoji as two.
 */
@Documented
@Constraint(validatedBy = MaxCodePointsValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxCodePoints {

    int value();

    String message() default "must be at most {value} characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
