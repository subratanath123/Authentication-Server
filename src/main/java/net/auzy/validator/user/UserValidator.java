package net.auzy.validator.user;

import net.auzy.dao.user.UserDao;
import net.auzy.entity.user.User;
import net.auzy.enums.Action;
import net.auzy.validator.annotation.UserCustomValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserValidator implements ConstraintValidator<UserCustomValidation, User> {

    @Autowired
    private UserDao userDao;

    private Action action;

    @Override
    public void initialize(UserCustomValidation constraintAnnotation) {
        action = constraintAnnotation.action();

        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(User value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (action == Action.SIGNUP_ACTION) {
            return validateSignUp(value, context);

        } else if (action == Action.SIGN_IN || action == Action.EMAIL_VERIFICATION) {
            return validateSignInOrEmailVerify(value, context);

        } else if (action == Action.FORGOT_PASSWORD) {
            return validateForgotPassword(value, context);

        } else if (action == Action.CHANGE_PASSWORD) {
            return validateChangePassword(value, context);
        }

        return false;
    }

    private boolean validateChangePassword(User user, ConstraintValidatorContext context) {
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            addErrorMessages(context, List.of("Password does not match"), "password");
            return false;
        }

        return true;
    }

    private boolean validateForgotPassword(User user, ConstraintValidatorContext context) {
        if (!userDao.isEmailExists(user.getEmail())) {
            addErrorMessages(context, List.of("There is no account with this email"), "email");
            return false;
        }

        return true;
    }

    public boolean validateSignUp(User user, ConstraintValidatorContext context) {
        boolean isValid = true;

        if (userDao.isEmailExists(user.getEmail())) {
            addErrorMessages(context, List.of("Email already registered. Use different email?"), "email");
            isValid = false;
        }

        User dbUser = userDao.findByEmail(user.getEmail());

        if (dbUser == null && !user.getPassword().equals(user.getConfirmPassword())) {
            addErrorMessages(context, List.of("Password does not match."), "confirmPassword");
            isValid = false;
        }

        return isValid;
    }

    public boolean validateSignInOrEmailVerify(User user, ConstraintValidatorContext context) {
        boolean isValid = true;

        User dbUser = userDao.findByEmail(user.getEmail());
        boolean emailExists = userDao.isEmailExists(user.getEmail());

        if (emailExists && dbUser.getPassword().equals(user.getPassword())) {
            addErrorMessages(context, List.of("Password does not match. Try Forgot Password?"), "email");
            isValid = false;
        }

        if (!emailExists) {
            addErrorMessages(context, List.of("Email is not registered. Do you want to signup?"), "email");
            isValid = false;
        }

        return isValid;
    }

    private void addErrorMessages(ConstraintValidatorContext context, List<String> errorMessages, String property) {
        context.buildConstraintViolationWithTemplate(String.join("\n", errorMessages))
                .addPropertyNode(property)
                .addConstraintViolation();
    }

}
