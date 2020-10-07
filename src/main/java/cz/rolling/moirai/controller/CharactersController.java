package cz.rolling.moirai.controller;

import cz.rolling.moirai.model.form.CharactersConfiguration;
import cz.rolling.moirai.model.form.WizardState;
import cz.rolling.moirai.service.ImportCsvParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
@RequestMapping({ "/characters" })
public class CharactersController {

    private final WizardState wizardState;

    private final ImportCsvParser importCsvParser;

    public CharactersController(WizardState wizardState,
                                ImportCsvParser importCsvParser) {
        this.wizardState = wizardState;
        this.importCsvParser = importCsvParser;
    }

    @GetMapping
    public ModelAndView characters() {
        ModelAndView mav = new ModelAndView("characters");
        mav.addObject("characterConfiguration", wizardState.getCharactersConfiguration());
        mav.addObject("charactersFileFormat", importCsvParser.getCharactersFileFormat());
        return mav;
    }

    @PostMapping("/import")
    public String importFile(@RequestParam("file") MultipartFile file) throws IOException {
        CharactersConfiguration config = wizardState.getCharactersConfiguration();
        config.setCharacterList(importCsvParser.parseCharacterList(file.getInputStream()));
        return "redirect:/characters";
    }

    @PostMapping("/previous")
    public String previous(){
        return "redirect:/configuration";
    }

    @PostMapping("/next")
    public String next(){
        return "redirect:/assignment";
    }
}