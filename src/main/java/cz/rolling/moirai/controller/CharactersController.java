package cz.rolling.moirai.controller;

import cz.rolling.moirai.WizardState;
import cz.rolling.moirai.model.Character;
import cz.rolling.moirai.model.CharactersConfiguration;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

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

    @PostMapping("/import")
    public String importFile(@RequestParam("file") MultipartFile file) throws IOException {
        CharactersConfiguration config = wizardState.getCharactersConfiguration();
        config.getCharacterList().clear();

        CSVFormat csvFormat = CSVFormat.RFC4180.withHeader(config.getHeaderColumnList().toArray(new String[0]));
        CSVParser csvParser = csvFormat.parse(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        for (CSVRecord record : csvParser) {
            Character newCharacter = new Character(record, config.getHeaderColumnList());
            config.getCharacterList().add(newCharacter);
        }

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
