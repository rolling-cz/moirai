package cz.rolling.moirai.exception;

public class UnknownCharacterImportException extends ImportException {
    private final long line;
    private String character;

    public UnknownCharacterImportException(long line, String character) {
        super("exception.unknown-character-import");
        this.character = character;
        this.line = line;
    }

    @Override
    public Object[] getParams() {
        return new Object[]{line, character};
    }
}
