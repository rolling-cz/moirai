package cz.rolling.moirai.model.form;

import cz.rolling.moirai.model.common.Character;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CharactersConfiguration {

    private List<Character> characterList = new ArrayList<>();

    public int getNumberOfCharacters() {
        return characterList.size();
    }

}
