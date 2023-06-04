package net.auzy.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.auzy.dao.user.UserDao;
import net.auzy.dto.common.Otp;
import net.auzy.entity.user.User;
import net.auzy.service.external.ProfileInfoVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static java.lang.String.format;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;
import static net.auzy.enums.RedisKey.PHONE_VERIFY_OTP;
import static net.auzy.utils.AuthenticationUtils.getLoggedInEmail;
import static net.auzy.utils.JsonUtils.convertJsonToObject;
import static net.auzy.utils.JsonUtils.convertObjectToJson;
import static org.springframework.http.HttpStatus.ACCEPTED;

@RestController
@RequestMapping("/secure")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true", allowedHeaders = "*")
public class SecureProfileInfoVerificationController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ProfileInfoVerificationService profileInfoVerificationService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping("/phone/otp/send")
    public ResponseEntity<?> sendPhoneVerification(@RequestBody Otp otp) throws JsonProcessingException {

        otp = profileInfoVerificationService.sendPhoneVerification(otp.getPhone());

        redisTemplate
                .opsForValue()
                .set(
                        PHONE_VERIFY_OTP.getKey(otp),
                        convertObjectToJson(otp),
                        ofMinutes(15)
                );

        return new ResponseEntity<>(ACCEPTED);
    }

    @PostMapping("/phone/otp/verify")
    public ResponseEntity<?> phoneVerification(@RequestBody Otp otp) {
        String email = getLoggedInEmail();

        User user = userDao.findByEmail(email);

        Otp actualOtp = getActualOtp(otp, email);

        assert actualOtp != null;

        user.setPhone(actualOtp.getPhone());

        userDao.save(user);

        return new ResponseEntity<>(ACCEPTED);
    }

    private Otp getActualOtp(Otp otp, String email) {
        String otpDetailsString = redisTemplate
                .opsForValue()
                .getAndExpire(
                        format(PHONE_VERIFY_OTP.getKey(otp)),
                        ofSeconds(10)
                );

        return convertJsonToObject(otpDetailsString, Otp.class);
    }

}
