package cz.rolling.moirai.controller;

import cz.rolling.moirai.WizardState;
import cz.rolling.moirai.model.CharactersConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping({ "/characters" })
public class CharactersController {

    private final WizardState wizardState;

    public CharactersController(WizardState wizardState) {
        this.wizardState = wizardState;
    }

    @GetMapping
    public ModelAndView characters() {
        ModelAndView mav = new ModelAndView("characters");
        mav.addObject("characterConfiguration", wizardState.getCharactersConfiguration());
        return mav;
    }

    @PostMapping("/previous")
    public String previous(@ModelAttribute CharactersConfiguration config){
        wizardState.setCharactersConfiguration(config);
        return "redirect:/configuration";
    }

    @PostMapping("/next")
    public String next(@ModelAttribute CharactersConfiguration config){
        wizardState.setCharactersConfiguration(config);
        return "redirect:/assignment";
    }
}
