package cz.rolling.moirai.model.common;

import lombok.Getter;

@Getter
public class CharacterAssignmentType {
    private final boolean isPreferred;
    private final int preferredRank;
    private final UnwantedAssignmentType unwantedAssignmentType;

    private CharacterAssignmentType(boolean isPreferred, int preferredRank, UnwantedAssignmentType unwantedAssignmentType) {
        this.isPreferred = isPreferred;
        this.preferredRank = preferredRank;
        this.unwantedAssignmentType = unwantedAssignmentType;
    }

    public static CharacterAssignmentType createPreferredType(int preferredRank) {
        return new CharacterAssignmentType(true, preferredRank, null);
    }

    public static CharacterAssignmentType createNotPreferredType(UnwantedAssignmentType unwantedAssignmentType) {
        return new CharacterAssignmentType(false, 0, unwantedAssignmentType);
    }
}
