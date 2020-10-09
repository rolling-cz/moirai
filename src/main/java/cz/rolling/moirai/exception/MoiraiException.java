package cz.rolling.moirai.exception;

public abstract class MoiraiException extends RuntimeException {
    MoiraiException(String message) {
        super(message);
    }

    public abstract Object[] getParams();
}
