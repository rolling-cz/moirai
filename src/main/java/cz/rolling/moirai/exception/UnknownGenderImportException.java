package cz.rolling.moirai.exception;

public class UnknownGenderImportException extends MoiraiException {
    private final String genderKey;
    private long line;

    public UnknownGenderImportException(long line, String genderKey) {
        super("exception.wrong-gender-import");
        this.genderKey = genderKey;
        this.line = line;
    }

    @Override
    public Object[] getParams() {
        return new Object[]{line, genderKey};
    }
}
