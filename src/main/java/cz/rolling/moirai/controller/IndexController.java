package cz.rolling.moirai.controller;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping({"/"})
public class IndexController {

    private final BuildProperties buildProperties;

    public IndexController(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @ModelAttribute("version")
    public String version() {
        return buildProperties.getVersion();
    }

    @GetMapping({"/"})
    public String index() {
        return "index";
    }

    @GetMapping({"/about"})
    public String about() {
        return "about";
    }

    @GetMapping({"/contact"})
    public String contact() {
        return "contact";
    }

    @GetMapping({"/faq"})
    public String faq() {
        return "faq";
    }
}
