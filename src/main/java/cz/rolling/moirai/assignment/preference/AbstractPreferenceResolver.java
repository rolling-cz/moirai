package cz.rolling.moirai.assignment.preference;

import cz.rolling.moirai.model.common.Assignment;
import cz.rolling.moirai.model.common.GenderAssignment;
import cz.rolling.moirai.model.common.Character;
import cz.rolling.moirai.model.common.User;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class AbstractPreferenceResolver implements PreferenceResolver{

    private final List<Character> characterList;
    private final List<User> userList;

    public AbstractPreferenceResolver(List<Character> characterList, List<User> userList) {
        this.characterList = characterList;
        this.userList = userList;
    }

    public GenderAssignment evaluateGenderAssignment(Assignment assignment) {
        User user = userList.get(assignment.getUserId());
        Character character = characterList.get(assignment.getCharId());

        return new GenderAssignment(
                user.getWantsPlayGender(),
                character.getGender(),
                PreferenceUtils.isCorrectGender(user, character) ? 0 : getRatingForGender()
        );
    }

    protected abstract int getRatingForGender();
}
