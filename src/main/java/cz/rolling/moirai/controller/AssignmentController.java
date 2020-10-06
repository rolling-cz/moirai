package cz.rolling.moirai.controller;

import cz.rolling.moirai.assignment.algorithm.AlgorithmFactory;
import cz.rolling.moirai.model.form.AlgorithmConfiguration;
import cz.rolling.moirai.model.form.WizardState;
import cz.rolling.moirai.service.ImportCsvParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping({"/assignment"})
public class AssignmentController {

    private final WizardState wizardState;

    private final List<AlgorithmFactory> algorithmFactorySet;

    private final ImportCsvParser importCsvParser;


    public AssignmentController(WizardState wizardState,
                                List<AlgorithmFactory> algorithmFactorySet,
                                ImportCsvParser importCsvParser) {
        this.wizardState = wizardState;
        this.algorithmFactorySet = algorithmFactorySet;
        this.importCsvParser = importCsvParser;
    }

    @GetMapping
    public ModelAndView assignment() {
        ModelAndView mav = new ModelAndView("algorithm");
        mav.addObject("usersFileFormat", importCsvParser.getUsersFileFormat(wizardState.getMainConfiguration()));
        mav.addObject("algorithmConfiguration", wizardState.getAlgorithmConfiguration());
        mav.addObject("algorithmFactorySet", filterFactories());
        return mav;
    }

    @PostMapping("/import")
    public String importFile(@RequestParam("file") MultipartFile file) throws IOException {
        AlgorithmConfiguration config = wizardState.getAlgorithmConfiguration();
        config.setUserList(importCsvParser.parseUserList(
                file.getInputStream(),
                wizardState.getMainConfiguration(),
                wizardState.getCharactersConfiguration().getCharacterList()
        ));
        return "redirect:/characters";
    }

    private List<AlgorithmFactory> filterFactories() {
        // TODO filter by feature labels
        return algorithmFactorySet;
    }

    @PostMapping("/previous")
    public String previous(@ModelAttribute AlgorithmConfiguration config) {
        wizardState.setAlgorithmConfiguration(config);
        return "redirect:/characters";
    }

    @PostMapping("/execute")
    public String execute(@ModelAttribute AlgorithmConfiguration config) {
        wizardState.setAlgorithmConfiguration(config);
        return "redirect:/execution";
    }
}
