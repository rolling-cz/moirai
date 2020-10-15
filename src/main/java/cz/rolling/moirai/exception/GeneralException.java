package cz.rolling.moirai.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GeneralException extends RuntimeException {
    private final HttpStatus status;

    public GeneralException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
