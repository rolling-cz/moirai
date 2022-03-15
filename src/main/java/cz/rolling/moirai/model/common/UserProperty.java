package cz.rolling.moirai.model.common;

public enum UserProperty {
    NAME("common.required-file-format.header.name"),
    SURNAME("common.required-file-format.header.surname"),
    GENDER("common.required-file-format.header.wanted-gender");

    private String key;

    UserProperty(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
