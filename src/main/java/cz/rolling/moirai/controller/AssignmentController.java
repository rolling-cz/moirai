package cz.rolling.moirai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping({ "/assignment" })
public class AssignmentController {

    @GetMapping
    public ModelAndView assignment() {
        return new ModelAndView("assignment");
    }
}
