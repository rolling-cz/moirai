package cz.rolling.moirai.model.form;

import cz.rolling.moirai.model.common.ApproachType;
import cz.rolling.moirai.validator.PreferenceFieldConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@PreferenceFieldConstraint(numberField = "numberOfPreferredCharacters", preferencesField = "ratingForPreferredCharacters")
@PreferenceFieldConstraint(numberField = "numberOfHatedCharacters", preferencesField = "ratingForHatedCharacters")
public class MainConfiguration {
    @NotNull
    private ApproachType approachType = ApproachType.CHARACTERS;

    private int ratingForNotSpecifiedChar = -10;
    private int ratingForGender = -100;

    @Min(1)
    @Max(15)
    private int numberOfPreferredCharacters = 3;

    @NotBlank
    private String ratingForPreferredCharacters = "7,5,3";

    @Min(0)
    @Max(15)
    private int numberOfHatedCharacters = 0;

    private String ratingForHatedCharacters = "-10";
}
