package net.auzy.service.external;

import net.auzy.dto.common.EmailOptions;
import net.auzy.service.tokenService.TokenGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountRecoveryService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenGeneratorService tokenGeneratorService;

    public void sendPasswordRecoveryLink(String email) {
        EmailOptions otpDetails = new EmailOptions
                .Builder()
                .msgBody("Please change your password by clicking this link: http://localhost:3000/change-password?token=" + tokenGeneratorService.generateAccessToken(email))
                .recipient(email)
                .subject("AUZY :: Password Recovery")
                .build();

        emailService.sendComplexMessage(otpDetails);
    }
}
