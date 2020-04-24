package com.xxx.api.apiscaffold.validate.annotation;

import com.xxx.api.apiscaffold.validate.OptionalStringValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ ElementType.FIELD, ElementType.TYPE })
@Constraint(validatedBy = OptionalStringValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface OptionalString {

    String[] value();
    String message() default "只能是规定范围内指定的值";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
