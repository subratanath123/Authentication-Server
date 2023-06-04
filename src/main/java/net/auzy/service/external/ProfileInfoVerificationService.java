package net.auzy.service.external;

import net.auzy.dto.common.EmailOptions;
import net.auzy.dto.common.Otp;
import net.auzy.dto.common.PhoneOption;
import net.auzy.utils.OtpGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static net.auzy.enums.VerificationType.EMAIL;
import static net.auzy.enums.VerificationType.PHONE;

@Service
public class ProfileInfoVerificationService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private PhoneService phoneService;

    public Otp sendPhoneVerification(String phone) {
        String verificationCode = OtpGenerator.generateOtp(6);
        PhoneOption otpDetails = new PhoneOption
                .Builder()
                .msgBody("Your OTP code is " + verificationCode)
                .recipient(phone)
                .verificationCode(verificationCode)
                .subject("AUZY :: OTP")
                .build();

        phoneService.sendSimpleMessage(otpDetails);

        return new Otp.Builder()
                .verificationType(PHONE)
                .phone(phone)
                .verificationCode(verificationCode)
                .build();
    }

    public Otp sendEmailVerification(String email) {
        String verificationCode = OtpGenerator.generateOtp(6);
        EmailOptions otpDetails = new EmailOptions
                .Builder()
                .msgBody("Your OTP code is " + verificationCode)
                .recipient(email)
                .verificationCode(verificationCode)
                .subject("AUZY :: OTP")
                .build();

        emailService.sendComplexMessage(otpDetails);

        return new Otp.Builder()
                .verificationType(EMAIL)
                .email(email)
                .verificationCode(verificationCode)
                .build();
    }
}
