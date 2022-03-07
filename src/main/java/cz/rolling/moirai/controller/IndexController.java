package cz.rolling.moirai.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping({"/"})
public class IndexController {

    @Value("${git.commit.id}")
    private String commitId;

    @Value("${git.commit.id.abbrev}")
    private String commitAbbrev;

    @Value("${git.build.time}")
    private String buildTime;

    @ModelAttribute("versionInfo")
    public Map<String, String> versionInfo() {
        Map<String, String> versionInfo = new HashMap<>();
        versionInfo.put("commitId", commitId);
        versionInfo.put("commitAbbrev", commitAbbrev);
        versionInfo.put("buildTime", StringUtils.split(buildTime, "T")[0]);
        return versionInfo;
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
