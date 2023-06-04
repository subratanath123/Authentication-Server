package net.auzy.controller.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin( origins = "http://localhost:3000" )
@Controller
@RequestMapping("/auth-login")
public class LoginController {

    @Value("${auzy.frontEndUrl}")
    private String auzyFrontEndServer;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping
    public String login(ModelMap modelMap) {
        return "login";
    }

    @PostMapping
    public String loginFailed() {
        return "redirect:/authenticate?error=invalid username or password";
    }
}
