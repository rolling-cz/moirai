package cz.rolling.moirai.model.common;

import lombok.Getter;

@Getter
public enum UnwantedAssignmentType {
    UNWANTED_CHAR("execution-results.header.unwanted-char-preference", false),
    UNWANTED_CHAR_GENDER("execution-results.header.unwanted-char-gender", false),
    UNWANTED_SINGLE_ROLE("execution-results.header.unwanted-role-single", true),
    UNWANTED_DOUBLE_ROLE("execution-results.header.unwanted-role-double", true);

    private final String key;

    private final boolean isForMoreRoles;

    UnwantedAssignmentType(String key, boolean isForMoreRoles) {
        this.key = key;
        this.isForMoreRoles = isForMoreRoles;
    }
}
