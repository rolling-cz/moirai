package cz.rolling.moirai.exception;

public class GeneralFormatImportException extends MoiraiException {

    private final String[] header;
    private final String error;

    public GeneralFormatImportException(String[] header, String error) {
        super("exception.general-format-import");
        this.header = header;
        this.error = error;
    }

    @Override
    public Object[] getParams() {
        return new Object[]{String.join(",", header), error};
    }
}
