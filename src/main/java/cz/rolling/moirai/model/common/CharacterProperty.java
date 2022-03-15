package cz.rolling.moirai.model.common;

public enum CharacterProperty {
    NAME("common.required-file-format.header.character"), GENDER("common.required-file-format.header.char-gender");

    private String key;

    CharacterProperty(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
