package cz.rolling.moirai.exception;

public class WizardValidationException extends MoiraiException {

    public WizardValidationException(String message) {
        super(message);
    }

    @Override
    public Object[] getParams() {
        return new Object[0];
    }
}
