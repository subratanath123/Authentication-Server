package net.auzy.controller.user;

import net.auzy.dao.user.UserDao;
import net.auzy.dto.common.EmailOptions;
import net.auzy.entity.user.User;
import net.auzy.service.external.EmailService;
import net.auzy.validator.annotation.UserCustomValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static net.auzy.enums.Action.CHANGE_PASSWORD;
import static net.auzy.utils.AuthenticationUtils.getLoggedInEmail;
import static org.springframework.http.HttpStatus.ACCEPTED;

@RestController
@RequestMapping("/secure")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true", allowedHeaders = "*")
@Validated
public class PasswordResetController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/change-password")
    public ResponseEntity<?> sendTemporaryPassword(@RequestBody
                                                   @UserCustomValidation(action = CHANGE_PASSWORD) User user) throws Exception {

        User dbUser = userDao.findByEmail(getLoggedInEmail());

        dbUser.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.save(dbUser);

        EmailOptions emailOptions = new EmailOptions
                .Builder()
                .msgBody("Password changed successfully")
                .recipient(dbUser.getEmail())
                .subject("AUZY :: Reset Password")
                .build();

        emailService.sendComplexMessage(emailOptions);

        return new ResponseEntity<>(ACCEPTED);
    }
}
