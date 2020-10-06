package cz.rolling.moirai.model.common;

public enum UserProperty {
    NAME("Name"),
    SURNAME("Surname"),
    GENDER("WantsPlayGender");

    private String key;

    UserProperty(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
