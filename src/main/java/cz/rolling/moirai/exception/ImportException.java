package cz.rolling.moirai.exception;

public abstract class ImportException extends RuntimeException {
    ImportException(String message) {
        super(message);
    }

    public abstract Object[] getParams();
}
