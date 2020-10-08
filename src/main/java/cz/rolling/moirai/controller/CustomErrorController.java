package cz.rolling.moirai.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ModelAndView handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Integer statusCode = status != null ? Integer.parseInt(status.toString()) : 500;

        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", statusCode);
        mav.addObject("message", "");

        return mav;
    }

    @Override
    public String getErrorPath() {
        // NOOP - deprecated method
        return null;
    }
}
