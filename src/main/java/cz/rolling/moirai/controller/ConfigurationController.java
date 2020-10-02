package cz.rolling.moirai.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.rolling.moirai.model.AssignmentConfiguration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Controller
@RequestMapping({ "/configuration" })
public class ConfigurationController {

    private static final String CONFIGURATION_JSON_FILE_NAME = "assignmentConfiguration.json";

    @GetMapping
    public ModelAndView configuration() {
        ModelAndView mav = new ModelAndView("configuration");
        mav.addObject("assignmentConfiguration", new AssignmentConfiguration());
        return mav;
    }

    @PostMapping
    public ModelAndView uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        AssignmentConfiguration config = new ObjectMapper().readValue(file.getBytes(), AssignmentConfiguration.class);
        ModelAndView mav = new ModelAndView("configuration");
        mav.addObject("assignmentConfiguration", config);
        return mav;
    }

    @PostMapping("/print")
    public ResponseEntity<Resource> generateJson(@ModelAttribute AssignmentConfiguration configuration) throws JsonProcessingException {
        byte[] buf = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsBytes(configuration);
        return ResponseEntity
                .ok()
                .contentLength(buf.length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header("Content-Disposition", "attachment; filename=\"" + CONFIGURATION_JSON_FILE_NAME + "\"")
                .body(new InputStreamResource(new ByteArrayInputStream(buf)));
    }
}
