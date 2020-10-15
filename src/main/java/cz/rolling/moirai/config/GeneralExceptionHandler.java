package cz.rolling.moirai.config;

import cz.rolling.moirai.exception.GeneralException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.Locale;

@ControllerAdvice
public class GeneralExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GeneralExceptionHandler.class);
    private final MessageSource messageSource;

    public GeneralExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception exception) {
        logger.error("Runtime exception", exception);
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", 500);
        mav.addObject("message", exception.getMessage());
        return mav;
    }

    @ExceptionHandler(GeneralException.class)
    public ModelAndView handleMoiraiException(GeneralException exception, Locale locale) {
        if (exception.getStatus().is5xxServerError()) {
            logger.error("General exception", exception);
        }
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", exception.getStatus().value());
        mav.addObject("message", messageSource.getMessage(exception.getMessage(), null, locale));
        return mav;
    }
}
