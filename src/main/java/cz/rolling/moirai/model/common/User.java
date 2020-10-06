package cz.rolling.moirai.model.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private final boolean wantsToPlaySingleRole = true;
    private final boolean wantsToPlayDoubleRole = false;
    private final Set<AssignmentWithRank> preferences = new HashSet<>();
    private int id;
    private String name;
    private String surname;
    private Gender wantsPlayGender;

    public void savePreference(AssignmentWithRank assignmentWithRank) {
        preferences.add(assignmentWithRank);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", wantsToPlaySingleRole=" + wantsToPlaySingleRole +
                ", wantsToPlayDoubleRole=" + wantsToPlayDoubleRole +
                '}';
    }
}
