package cz.rolling.moirai.controller;

import cz.rolling.moirai.WizardState;
import cz.rolling.moirai.model.AlgorithmConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping({ "/assignment" })
public class AssignmentController {

    private final WizardState wizardState;

    public AssignmentController(WizardState wizardState) {
        this.wizardState = wizardState;
    }

    @GetMapping
    public ModelAndView assignment() {
        ModelAndView mav = new ModelAndView("algorithm");
        mav.addObject("algorithmConfiguration", wizardState.getAlgorithmConfiguration());
        return mav;
    }

    @PostMapping("/previous")
    public String previous(@ModelAttribute AlgorithmConfiguration config){
        wizardState.setAlgorithmConfiguration(config);
        return "redirect:/characters";
    }

    @PostMapping("/execute")
    public String next(@ModelAttribute AlgorithmConfiguration config){
        wizardState.setAlgorithmConfiguration(config);
        return "redirect:/assignment";
    }
}
