package cz.rolling.moirai.model;

public enum CharacterProperty {
    NAME("CharacterName"), GENDER("Gender");

    private String key;

    CharacterProperty(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
