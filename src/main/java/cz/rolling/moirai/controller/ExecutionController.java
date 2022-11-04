package cz.rolling.moirai.controller;

import cz.rolling.moirai.assignment.helper.SolutionHolder;
import cz.rolling.moirai.exception.GeneralException;
import cz.rolling.moirai.model.common.ApproachType;
import cz.rolling.moirai.model.common.PrintableAssignment;
import cz.rolling.moirai.model.common.result.ResultSummary;
import cz.rolling.moirai.model.form.WizardState;
import cz.rolling.moirai.service.AlgorithmExecutor;
import cz.rolling.moirai.service.SolutionCsvPrinter;
import cz.rolling.moirai.service.SolutionDisplayService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
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

    private final SolutionDisplayService solutionDisplayService;

    public ExecutionController(WizardState wizardState,
                               AlgorithmExecutor algorithmExecutor,
                               SolutionCsvPrinter solutionCsvPrinter,
                               SolutionDisplayService solutionDisplayService) {
        this.wizardState = wizardState;
        this.algorithmExecutor = algorithmExecutor;
        this.solutionCsvPrinter = solutionCsvPrinter;
        this.solutionDisplayService = solutionDisplayService;
    }

    @GetMapping({"/process"})
    public String execution() throws ClassNotFoundException {
        SolutionHolder solutionHolder = algorithmExecutor.executeAlgorithm(wizardState);
        wizardState.setSolutionList(solutionHolder.getSolutions());
        wizardState.setDistributionHeaderList(solutionHolder.getDistributionHeaderList());
        return "redirect:/execution";
    }

    @GetMapping("/previous")
    public String previous() {
        return "redirect:/assignment";
    }

    @GetMapping
    public ModelAndView results() {
        ModelAndView mav = new ModelAndView("execution");
        mav.addObject("solutions", wizardState.getSolutionList());
        mav.addObject("hasSolution", wizardState.getSolutionList().stream().anyMatch(ResultSummary::isHasSolution));

        mav.addObject("approachType", wizardState.getMainConfiguration().getApproachType());
        mav.addObject("headerList", wizardState.getDistributionHeaderList());
        boolean hasNegativeDist = false;
        if (wizardState.getDistributionHeaderList() != null) {
            hasNegativeDist = wizardState.getDistributionHeaderList().stream().anyMatch(d -> !d.isPositive());
        }
        mav.addObject("hasNegativeDist", hasNegativeDist);
        return mav;
    }

    @GetMapping({"/solution/{index}/display"})
    public ModelAndView displayDetail(@PathVariable("index") int index) {
        List<ResultSummary> solutionList = wizardState.getSolutionList();
        if (solutionList == null || index < 0 || index >= solutionList.size()) {
            throw new GeneralException(HttpStatus.NOT_FOUND, "exception.solution-does-not-exist");
        }

        ApproachType approachType = wizardState.getMainConfiguration().getApproachType();
        List<PrintableAssignment> assignments = solutionDisplayService.getDetailAssignments(
                wizardState.getAlgorithmConfiguration().getUserList(),
                wizardState.getCharactersConfiguration().getCharacterList(),
                approachType,
                solutionList.get(index));

        String viewName = approachType == ApproachType.CONTENT ? "solutionDetailContent" : "solutionDetailCharacters";
        ModelAndView mav = new ModelAndView(viewName);
        mav.addObject("assignments", assignments);
        mav.addObject("attributes", wizardState.getMainConfiguration().getAttributeList());
        mav.addObject("existLabels", !wizardState.getMainConfiguration().getLabelList().isEmpty());
        return mav;
    }

    @GetMapping({"/solution/{index}/download"})
    public ResponseEntity<Resource> download(@PathVariable("index") int index) throws IOException {
        List<ResultSummary> solutionList = wizardState.getSolutionList();
        if (index < 0 || index >= solutionList.size()) {
            return ResponseEntity.notFound().build();
        }
        ResultSummary solution = solutionList.get(index);

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
