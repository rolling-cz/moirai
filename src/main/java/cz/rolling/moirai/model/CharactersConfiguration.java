package cz.rolling.moirai.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CharactersConfiguration {

    private List<Character> characterList = new ArrayList<>();

    private List<String> headerColumnList = new ArrayList<>();

    public CharactersConfiguration() {
        headerColumnList.add(CharacterProperty.NAME.getKey());
        headerColumnList.add(CharacterProperty.GENDER.getKey());
    }

    public int getNumberOfCharacters() {
        return characterList.size();
    }

    public String getCharacterFormat() {
        return String.join(",", headerColumnList);
    }
}
