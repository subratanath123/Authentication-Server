package net.auzy.validator.user;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.auzy.dto.common.EmailOptions;
import net.auzy.dto.common.Otp;
import net.auzy.enums.Action;
import net.auzy.utils.JsonUtils;
import net.auzy.validator.annotation.MailVerification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static net.auzy.enums.RedisKey.EMAIL_VERIFY_OTP;

@Component
public class MailVerificationValidator implements ConstraintValidator<MailVerification, Otp> {

    private Action action;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void initialize(MailVerification constraintAnnotation) {
        action = constraintAnnotation.action();

        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Otp otp, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (action == Action.EMAIL_VERIFICATION) {
            return validateMailVerification(otp, context);

        } else if (action == Action.EMAIL_RESEND || action == Action.EMAIL_CHANGE) {
            return validateSignedUpEmail(otp, context);
        }

        return true;
    }

    public boolean validateMailVerification(Otp otp, ConstraintValidatorContext context) {

        String otpDetailsString = redisTemplate
                .opsForValue()
                .get(EMAIL_VERIFY_OTP.getKey(otp));

        if (otpDetailsString == null) {
            addErrorMessages(context, List.of("Url Expired"), "verificationCode");
            return false;
        }

        EmailOptions otpDetails = JsonUtils.convertJsonToObject(otpDetailsString, EmailOptions.class);

        if (otpDetails == null) {
            addErrorMessages(context, List.of("Invalid request made"), "verificationCode");
            return false;

        } else if (!otpDetails.getVerificationCode().equals(otp.getVerificationCode())) {
            addErrorMessages(context, List.of("Wrong Verification Code"), "verificationCode");
            return false;
        }

        return true;
    }

    private boolean validateSignedUpEmail(Otp otp, ConstraintValidatorContext context) {

        String otpDetailsString = redisTemplate
                .opsForValue()
                .get(EMAIL_VERIFY_OTP.getKey(otp));

        if (otpDetailsString == null) {
            addErrorMessages(context, List.of("Url Expired"), "email");
            return false;
        }

        EmailOptions otpDetails = JsonUtils.convertJsonToObject(otpDetailsString, EmailOptions.class);

        if (otpDetails == null) {
            addErrorMessages(context, List.of("mail is not registered. Do you want to change email?"), "email");
            return false;
        }

        return true;
    }

    private void addErrorMessages(ConstraintValidatorContext context, List<String> errorMessages, String property) {
        context.buildConstraintViolationWithTemplate(String.join("\n", errorMessages))
                .addPropertyNode(property)
                .addConstraintViolation();
    }
}
