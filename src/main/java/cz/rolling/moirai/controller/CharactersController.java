package cz.rolling.moirai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping({ "/characters" })
public class CharactersController {

    @GetMapping
    public ModelAndView characters() {
        ModelAndView mav = new ModelAndView("characters");
        return mav;
    }
}
