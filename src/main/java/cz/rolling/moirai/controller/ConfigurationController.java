package cz.rolling.moirai.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.rolling.moirai.exception.GeneralException;
import cz.rolling.moirai.model.common.CharacterAttribute;
import cz.rolling.moirai.model.common.UiStyle;
import cz.rolling.moirai.model.form.AlgorithmConfiguration;
import cz.rolling.moirai.model.form.CharactersConfiguration;
import cz.rolling.moirai.model.form.MainConfiguration;
import cz.rolling.moirai.model.form.WizardState;
import cz.rolling.moirai.service.WizardValidator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping({ "/configuration" })
public class ConfigurationController {

    private static final String CONFIGURATION_JSON_FILE_NAME = "mainConfiguration.json";

    private final Logger logger = LoggerFactory.getLogger(ConfigurationController.class);
    private final WizardState wizardState;
    private final WizardValidator wizardValidator;

    public ConfigurationController(WizardState wizardState, WizardValidator wizardValidator) {
        this.wizardState = wizardState;
        this.wizardValidator = wizardValidator;
    }

    @ModelAttribute("mainConfiguration")
    public MainConfiguration mainConfiguration() {
        return wizardState.getMainConfiguration();
    }

    @GetMapping
    public String configuration(@RequestParam(value = "uiStyle", required = false) String uiStyleAsString) {
        applyUiStyle(uiStyleAsString);
        return getTemplate();
    }

    private void applyUiStyle(String uiStyleAsString) {
        if (StringUtils.isNotBlank(uiStyleAsString)) {
            try {
                UiStyle uiStyle = UiStyle.valueOf(uiStyleAsString);
                wizardState.setUiStyle(uiStyle);
            } catch (IllegalArgumentException e) {
                logger.error("Unknown ui style " + uiStyleAsString);
            }
        }
    }

    @GetMapping("/remove/{index}")
    public String remove(@PathVariable int index) {
        List<CharacterAttribute> attributeList = wizardState.getMainConfiguration().getAttributeList();
        if (index >= 0 && index <= attributeList.size()) {
            attributeList.remove(index);
        }
        return getTemplate();
    }

    @GetMapping("/new")
    public String reset(@RequestParam(value = "uiStyle", required = false) String uiStyleAsString){
        wizardState.setMainConfiguration(new MainConfiguration());
        wizardState.setCharactersConfiguration(new CharactersConfiguration());
        wizardState.setAlgorithmConfiguration(new AlgorithmConfiguration());
        wizardState.setSolutionList(null);
        wizardState.setDistributionHeaderList(null);
        applyUiStyle(uiStyleAsString);
        return "redirect:/configuration";
    }

    @PostMapping("/next")
    public String save(@Valid MainConfiguration mainConfiguration, BindingResult bindingResult){
        wizardState.setMainConfiguration(mainConfiguration);
        wizardValidator.validateMainConfig(mainConfiguration, bindingResult);

        if (bindingResult.hasErrors()) {
            return getTemplate();
        }
        return "redirect:/characters";
    }

    @PostMapping("/import")
    public String importFile(@RequestParam("file") MultipartFile file) {
        MainConfiguration config;
        try {
            config = new ObjectMapper().readValue(file.getBytes(), MainConfiguration.class);
        } catch (IOException e) {
            throw new GeneralException(HttpStatus.BAD_REQUEST, "exception.incorrect_config_format");
        }
        wizardState.setMainConfiguration(config);
        return "redirect:/configuration";
    }

    @PostMapping("/print")
    public ResponseEntity<Resource> generateJson(@ModelAttribute MainConfiguration config) throws JsonProcessingException {
        wizardState.setMainConfiguration(config);
        byte[] buf = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsBytes(config);
        return ResponseEntity
                .ok()
                .contentLength(buf.length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header("Content-Disposition", "attachment; filename=\"" + CONFIGURATION_JSON_FILE_NAME + "\"")
                .body(new InputStreamResource(new ByteArrayInputStream(buf)));
    }

    private String getTemplate() {
        switch (wizardState.getUiStyle()) {
            case BASIC:
                return "configurationBasic";
            case CUSTOM:
                return "configurationCustom";
            default:
                throw new IllegalArgumentException("Unknown ui style " + wizardState.getUiStyle());
        }
    }
}
