package cz.rolling.moirai.controller;

import cz.rolling.moirai.assignment.helper.SolutionHolder;
import cz.rolling.moirai.model.common.VerboseSolution;
import cz.rolling.moirai.model.form.WizardState;
import cz.rolling.moirai.service.AlgorithmExecutor;
import cz.rolling.moirai.service.SolutionCsvPrinter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping({"/execution"})
public class ExecutionController {

    private static final String SOLUTION_JSON_FILE_NAME = "solution.csv";

    private final WizardState wizardState;

    private final AlgorithmExecutor algorithmExecutor;

    private final SolutionCsvPrinter solutionCsvPrinter;

    public ExecutionController(WizardState wizardState,
                               AlgorithmExecutor algorithmExecutor,
                               SolutionCsvPrinter solutionCsvPrinter) {
        this.wizardState = wizardState;
        this.algorithmExecutor = algorithmExecutor;
        this.solutionCsvPrinter = solutionCsvPrinter;
    }

    @GetMapping({"/process"})
    public String execution() throws ClassNotFoundException {
        SolutionHolder solutionHolder = algorithmExecutor.executeAlgorithm(wizardState);
        wizardState.setSolutionList(solutionHolder.getSolutions());
        wizardState.setDistributionHeaderList(solutionHolder.getDistributionHeaderList());
        return "redirect:/execution";
    }

    @GetMapping
    public ModelAndView results() {
        ModelAndView mav = new ModelAndView("execution");
        mav.addObject("solutions", wizardState.getSolutionList());
        mav.addObject("approachType", wizardState.getMainConfiguration().getApproachType());
        mav.addObject("headerList", wizardState.getDistributionHeaderList());
        mav.addObject("hasNegativeDist", wizardState.getDistributionHeaderList().stream().anyMatch(d -> !d.isPositive()));
        return mav;
    }

    @GetMapping({"/solution/{index}"})
    public ResponseEntity<Resource> download(@PathVariable("index") int index) throws IOException {
        List<VerboseSolution> solutionList = wizardState.getSolutionList();
        if (index < 0 || index >= solutionList.size()) {
            return ResponseEntity.notFound().build();
        }
        VerboseSolution solution = solutionList.get(index);

        byte[] buf = solutionCsvPrinter.printSolution(
                solution,
                wizardState.getAlgorithmConfiguration().getUserList(),
                wizardState.getCharactersConfiguration().getCharacterList()
        );

        return ResponseEntity
                .ok()
                .contentLength(buf.length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header("Content-Disposition", "attachment; filename=\"" + SOLUTION_JSON_FILE_NAME + "\"")
                .body(new InputStreamResource(new ByteArrayInputStream(buf)));
    }
}
