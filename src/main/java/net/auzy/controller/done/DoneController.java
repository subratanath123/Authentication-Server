package net.auzy.controller.done;


import net.auzy.dto.common.DoneBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/public")
public class DoneController {

    @GetMapping("/done")
    public String showDoneMessage(@ModelAttribute DoneBean doneBean) {

        return "done";
    }
}
