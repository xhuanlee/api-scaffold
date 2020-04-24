package com.xxx.api.apiscaffold.validate;


import com.xxx.api.apiscaffold.validate.annotation.OptionalInteger;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OptionalIntegerValidator implements ConstraintValidator<OptionalInteger, Integer> {

    private int[] values;

    @Override
    public void initialize(OptionalInteger constraintAnnotation) {
        values = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        // 值为空时不校验
        if (integer == null) {
            return true;
        }

        for (int v : values) {
            if (v == integer) {
                return true;
            }
        }

        return false;
    }
}
