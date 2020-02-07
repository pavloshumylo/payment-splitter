package com.eleks.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.stream.Stream;

public class CurrencyValidator implements ConstraintValidator<ValidateCurrency, String> {

    @Override
    public void initialize(ValidateCurrency constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Stream.of("UA", "USD", "EUR").anyMatch(currency -> currency.equalsIgnoreCase(value));
    }
}
