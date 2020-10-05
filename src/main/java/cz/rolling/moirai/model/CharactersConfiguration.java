package cz.rolling.moirai.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CharactersConfiguration {

    private List<Character> characterList = new ArrayList<>();
}
