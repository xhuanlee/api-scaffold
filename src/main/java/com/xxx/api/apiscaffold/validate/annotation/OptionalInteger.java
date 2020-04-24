package com.xxx.api.apiscaffold.validate.annotation;


import com.xxx.api.apiscaffold.validate.OptionalIntegerValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ ElementType.FIELD, ElementType.TYPE })
@Constraint(validatedBy = OptionalIntegerValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface OptionalInteger {

    int[] value();
    String message() default "只能是规定范围内指定的值";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
