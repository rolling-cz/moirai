package cz.rolling.moirai.exception;

public class WrongLabelImportException extends MoiraiException {
    private final long line;
    private final String labelName;

    public WrongLabelImportException(long line, String labelName) {
        super("exception.wrong-label-import");
        this.line = line;
        this.labelName = labelName;
    }

    @Override
    public Object[] getParams() {
        return new Object[]{line, labelName};
    }
}
