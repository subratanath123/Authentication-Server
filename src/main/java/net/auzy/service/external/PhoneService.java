package net.auzy.service.external;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import net.auzy.dto.common.PhoneOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PhoneService implements ExternalMessageService<PhoneOption> {

    @Value("${twillo.account.sid}")
    private String accountSid;

    @Value("${twillo.auth.token}")
    private String authToken;

    @Value("${twillo.phone.number}")
    private String twilloPhoneNumber;

    public String sendSimpleMessage(PhoneOption phoneOption) {

        try {

            Twilio.init("AC0f93deae7e01543e3ef006595dd9f925",
                    "1171274ae6868250dded4c91d098dfe3");

            Message.creator(
                    new PhoneNumber(phoneOption.getTargetDestination()),
                    new PhoneNumber(twilloPhoneNumber),
                    phoneOption.getMsgBody()
            ).create();

            return "Otp Sent Successfully...";

        } catch (Exception e) {
            return "Error while Sending Otp to phone number";
        }
    }

    @Override
    public String sendComplexMessage(PhoneOption details) {
        throw new RuntimeException();
    }

}