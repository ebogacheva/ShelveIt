package org.bogacheva.training.service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class StorageTypeValidator implements ConstraintValidator<ValidStorageType, Enum<?>> {

    private Enum<?>[] validValues;

    @Override
    public void initialize(ValidStorageType constraintAnnotation) {
        validValues = constraintAnnotation.enumClass().getEnumConstants();
    }

    @Override
    public boolean isValid(Enum<?> anEnum, ConstraintValidatorContext constraintValidatorContext) {
        return anEnum != null && Arrays.asList(validValues).contains(anEnum);
    }
}
