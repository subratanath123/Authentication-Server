package net.auzy.validator.user;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import net.auzy.dto.common.Otp;
import net.auzy.enums.Action;
import net.auzy.utils.JsonUtils;
import net.auzy.validator.annotation.PhoneVerification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static net.auzy.enums.RedisKey.PHONE_VERIFY_OTP;

@Component
public class PhoneVerificationValidator implements ConstraintValidator<PhoneVerification, Otp> {

    private Action action;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void initialize(PhoneVerification constraintAnnotation) {
        action = constraintAnnotation.action();

        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Otp otp, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (action == Action.PHONE_VERIFICATION) {
            return validatePhoneVerification(otp, context);
        }

        return true;
    }

    public boolean validatePhoneVerification(Otp otp, ConstraintValidatorContext context) {

        String otpDetailsString = redisTemplate
                .opsForValue()
                .get(PHONE_VERIFY_OTP.getKey(otp));

        if (otpDetailsString == null) {
            addErrorMessages(context, List.of("OTP Expired"), "verificationCode");
            return false;
        }

        Otp otpDetails = JsonUtils.convertJsonToObject(otpDetailsString, Otp.class);

        if (otpDetails == null) {
            addErrorMessages(context, List.of("Invalid request made"), "verificationCode");
            return false;

        } else if (!otpDetails.getVerificationCode().equals(otp.getVerificationCode())) {
            addErrorMessages(context, List.of("Wrong Verification Code"), "verificationCode");
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
