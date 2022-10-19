package cz.rolling.moirai.controller;

import cz.rolling.moirai.assignment.algorithm.AlgorithmFactory;
import cz.rolling.moirai.assignment.algorithm.AlgorithmFeature;
import cz.rolling.moirai.exception.MoiraiException;
import cz.rolling.moirai.model.form.AlgorithmConfiguration;
import cz.rolling.moirai.model.form.WizardState;
import cz.rolling.moirai.service.AlgorithmFeatureService;
import cz.rolling.moirai.service.ImportCsvParser;
import cz.rolling.moirai.service.WizardValidator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping({"/assignment"})
public class AssignmentController {

    private final WizardState wizardState;
    private final List<AlgorithmFactory> algorithmFactorySet;
    private final ImportCsvParser importCsvParser;
    private final WizardValidator wizardValidator;
    private final AlgorithmFeatureService algorithmFeatureService;

    public AssignmentController(WizardState wizardState,
                                List<AlgorithmFactory> algorithmFactorySet,
                                ImportCsvParser importCsvParser,
                                WizardValidator wizardValidator,
                                AlgorithmFeatureService algorithmFeatureService) {
        this.wizardState = wizardState;
        this.algorithmFactorySet = algorithmFactorySet;
        this.importCsvParser = importCsvParser;
        this.wizardValidator = wizardValidator;
        this.algorithmFeatureService = algorithmFeatureService;
    }

    @GetMapping
    public ModelAndView assignment(Locale locale) {
        return getPageModelAndView(locale);
    }

    @PostMapping("/import")
    public String importFile(@RequestParam("file") MultipartFile file, Locale locale) throws IOException {
        AlgorithmConfiguration config = wizardState.getAlgorithmConfiguration();
        config.setUserList(importCsvParser.parseUserList(
                file.getInputStream(),
                wizardState.getMainConfiguration(),
                wizardState.getCharactersConfiguration().getCharacterList(),
                locale
        ));
        return "redirect:/assignment";
    }

    @PostMapping("/previous")
    public String previous(@ModelAttribute AlgorithmConfiguration config) {
        saveConfig(config);
        return "redirect:/characters";
    }

    @PostMapping("/execute")
    public String execute(@ModelAttribute AlgorithmConfiguration config) {
        saveConfig(config);
        wizardValidator.validate(wizardState);
        return "redirect:/execution/process";
    }

    @ExceptionHandler(MoiraiException.class)
    public ModelAndView handleException(MoiraiException exception, Locale locale) {
        ModelAndView mav = getPageModelAndView(locale);
        mav.addObject("errorMessage", exception.getMessage());
        mav.addObject("errorParams", exception.getParams());
        return mav;
    }

    private ModelAndView getPageModelAndView(Locale locale) {
        ModelAndView mav = new ModelAndView("algorithm");
        mav.addObject("usersFileFormat",
                importCsvParser.getUsersFileFormat(wizardState.getMainConfiguration(), locale)
        );
        mav.addObject("algorithmConfiguration", wizardState.getAlgorithmConfiguration());
        mav.addObject("algorithmFactorySet", filterFactories());
        mav.addObject("numberOfCharacters", wizardState.getCharactersConfiguration().getNumberOfCharacters());
        mav.addObject("errorMessage", "");
        mav.addObject("errorParams", null);
        return mav;
    }

    private List<AlgorithmFactory> filterFactories() {
        Set<AlgorithmFeature> requiredFeatureSet = algorithmFeatureService.determineRequiredFeatureSet(wizardState);
        return algorithmFactorySet.stream()
                .filter(factory -> factory.getSupportedFeatures().containsAll(requiredFeatureSet))
                .collect(Collectors.toList());
    }

    private void saveConfig(AlgorithmConfiguration newConfig) {
        AlgorithmConfiguration currentConfig = wizardState.getAlgorithmConfiguration();
        currentConfig.setAlgorithmFactoryName(newConfig.getAlgorithmFactoryName());
    }
}
