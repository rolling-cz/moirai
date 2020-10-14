package cz.rolling.moirai.exception;

import cz.rolling.moirai.model.common.CharacterAttribute;

public class WrongAttributeValueImportException extends MoiraiException {
    private final long line;
    private final String value;
    private final CharacterAttribute attr;

    public WrongAttributeValueImportException(long line, String value, CharacterAttribute attr) {
        super("exception.wrong-attr-value-import");
        this.value = value;
        this.attr = attr;
        this.line = line;
    }

    @Override
    public Object[] getParams() {
        return new Object[]{line, attr.getName(), attr.getMin(), attr.getMax(), value};
    }
}