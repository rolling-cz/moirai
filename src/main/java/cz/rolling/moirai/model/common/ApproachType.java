package cz.rolling.moirai.model.common;

public enum ApproachType {
    /*CONTENT("assignment-config.approach.content"),*/ CHARACTERS("assignment-config.approach.characters");

    private final String key;

    ApproachType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
