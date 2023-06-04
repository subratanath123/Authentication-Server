package net.auzy.controller.user;


import com.fasterxml.jackson.core.JsonProcessingException;
import net.auzy.dao.user.UserDao;
import net.auzy.dto.common.EmailOptions;
import net.auzy.dto.common.Otp;
import net.auzy.entity.user.User;
import net.auzy.service.external.AccountRecoveryService;
import net.auzy.service.external.ProfileInfoVerificationService;
import net.auzy.service.user.UserService;
import net.auzy.utils.JsonUtils;
import net.auzy.validator.annotation.MailVerification;
import net.auzy.validator.annotation.UserCustomValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

import static java.lang.String.format;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;
import static net.auzy.entity.user.Role.PROFILE;
import static net.auzy.enums.Action.*;
import static net.auzy.enums.RedisKey.EMAIL_VERIFY_OTP;
import static net.auzy.utils.JsonUtils.convertObjectToJson;
import static org.springframework.http.HttpStatus.ACCEPTED;

@RestController
@RequestMapping("/public")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true", allowedHeaders = "*")
@Validated //its must needed for custom annotation validator
public class SignupController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountRecoveryService accountRecoveryService;

    @Autowired
    private ProfileInfoVerificationService profileInfoVerificationService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${auzy.frontEndUrl}")
    private String auzyFrontEndServer;

    @GetMapping("/app-status")
    public String appStatus() {
        return "OK";
    }

    @GetMapping("/registration")
    public RedirectView gotToRegistration() {
        return new RedirectView(auzyFrontEndServer);
    }

    @GetMapping("/forgot-password")
    public RedirectView gotToForgotPassword() {
        return new RedirectView(auzyFrontEndServer + "/forgot-password");
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registrationSubmit(@Validated
                                                @RequestBody
                                                @UserCustomValidation(action = SIGNUP_ACTION) User user) throws Exception {
        user.setRoleList(List.of(PROFILE));
        user.setVerifiedUser(false);

//        redisTemplate.convertAndSend("signUpLog", convertObjectToJson(user));

        userService.save(user);

        Otp otpDetails = profileInfoVerificationService.sendEmailVerification(user.getEmail());

        redisTemplate
                .opsForValue()
                .set(
                        EMAIL_VERIFY_OTP.getKey(otpDetails),
                        convertObjectToJson(otpDetails),
                        ofMinutes(15)
                );

        return new ResponseEntity<>(user, ACCEPTED);
    }

    @PostMapping("/mail-verification")
    public ResponseEntity<?> mailVerification(@Validated
                                              @MailVerification(action = EMAIL_VERIFICATION)
                                              @RequestBody EmailOptions otpDetails) {

        User user = userDao.findByEmail(otpDetails.getTargetDestination());
        user.setVerifiedUser(true);
        userDao.save(user);

        return new ResponseEntity<>(ACCEPTED);
    }

    @PostMapping("/mail-resend")
    public ResponseEntity<?> mailResend(@Validated
                                        @RequestBody
                                        @MailVerification(action = EMAIL_RESEND) Otp otp) throws JsonProcessingException {

        Otp otpDetails = profileInfoVerificationService.sendEmailVerification(otp.getEmail());

        redisTemplate
                .opsForValue()
                .set(
                        EMAIL_VERIFY_OTP.getKey(otpDetails),
                        JsonUtils.convertObjectToJson(otpDetails),
                        ofMinutes(15)
                );

        return new ResponseEntity<>(ACCEPTED);
    }

    @PostMapping("/mail-change") //ToDo previous email should be removed and redirected
    public ResponseEntity<?> mailChange(@Validated
                                        @RequestBody
                                        @MailVerification(action = EMAIL_CHANGE) EmailOptions emailOptions) {

        return new ResponseEntity<>(ACCEPTED);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> sendTemporaryPassword(@RequestBody
                                                   @UserCustomValidation(action = FORGOT_PASSWORD) User user) {

        accountRecoveryService.sendPasswordRecoveryLink(user.getEmail());

        return new ResponseEntity<>(ACCEPTED);
    }

}
