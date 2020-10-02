package cz.rolling.moirai.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AssignmentConfiguration {
    private ApproachType approachType = ApproachType.CHARACTERS;

    private int ratingForGender = -100;

    private int numberOfPreferredCharacters = 3;

    private String ratingForPreferredCharacters = "7,5,3";

    private int numberOfHatedCharacters = 1;

    private String ratingForHatedCharacters = "-10";
}
