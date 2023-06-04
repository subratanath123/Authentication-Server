package net.auzy.validator.annotation;

import net.auzy.enums.Action;
import net.auzy.validator.user.MailVerificationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;

@Target({ElementType.TYPE, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MailVerificationValidator.class)
public @interface MailVerification {
    String message() default "Invalid value";

    Action action();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
