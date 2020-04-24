package com.xxx.api.apiscaffold.validate;

import com.xxx.api.apiscaffold.validate.annotation.OptionalString;
import io.micrometer.core.instrument.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OptionalStringValidator implements ConstraintValidator<OptionalString, String> {

    private String[] values;

    @Override
    public void initialize(OptionalString constraintAnnotation) {
        values = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        // 值为空时不校验
        if (StringUtils.isBlank(s)) {
            return true;
        }

        for (String v : values) {
            if (v.equals(s)) {
                return true;
            }
        }

        return false;
    }
}
