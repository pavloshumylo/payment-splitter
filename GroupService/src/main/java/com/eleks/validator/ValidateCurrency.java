package com.eleks.validator;

import static java.lang.annotation.ElementType.FIELD;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = {CurrencyValidator.class})
@Target( {FIELD} )
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateCurrency {

    String message() default "Supported currencies: UA, USD, EUR";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
