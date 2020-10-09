package cz.rolling.moirai.exception;

public class WrongLineImportException extends ImportException {
    private final String[] header;
    private final long line;

    public WrongLineImportException(long line, String[] header) {
        super("exception.wrong-line-import");
        this.header = header;
        this.line = line;
    }

    @Override
    public Object[] getParams() {
        return new Object[]{line, String.join(",", header)};
    }
}
