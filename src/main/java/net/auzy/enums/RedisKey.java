package net.auzy.enums;

import net.auzy.dto.common.Otp;

import java.util.function.Function;

import static net.auzy.utils.AuthenticationUtils.getLoggedInEmail;

public enum RedisKey {

    EMAIL_VERIFY_OTP(otp -> String.format("otpDetails:verify:user:phone:%s", getLoggedInEmail())),
    PHONE_VERIFY_OTP(otp -> String.format("otpDetails:verify:user:email:%s", getLoggedInEmail()));

    RedisKey(Function<Otp, String> keyResolver) {
        this.keyResolver = keyResolver;
    }

    private Function<Otp, String> keyResolver;

    public Function<Otp, String> getKeyResolver() {
        return keyResolver;
    }

    public String getKey(Otp otp) {
        return getKeyResolver().apply(otp);
    }
}
