package cz.rolling.moirai.controller;

import cz.rolling.moirai.assignment.algorithm.AlgorithmFactory;
import cz.rolling.moirai.assignment.helper.SolutionHolder;
import cz.rolling.moirai.model.form.WizardState;
import cz.rolling.moirai.service.AlgorithmExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping({"/execution"})
public class ExecutionController {

    private final WizardState wizardState;

    private final AlgorithmExecutor algorithmExecutor;

    public ExecutionController(WizardState wizardState,
                                List<AlgorithmFactory> algorithmFactorySet,
                                AlgorithmExecutor algorithmExecutor) {
        this.wizardState = wizardState;
        this.algorithmExecutor = algorithmExecutor;
    }

    @GetMapping
    public ModelAndView execution() throws ClassNotFoundException {
        SolutionHolder solutionHandler = algorithmExecutor.executeAlgorithm(wizardState);

        ModelAndView mav = new ModelAndView("execution");
        mav.addObject("solutions", solutionHandler);
        return mav;
    }
}
