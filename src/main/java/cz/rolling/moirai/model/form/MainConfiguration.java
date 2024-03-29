package cz.rolling.moirai.model.form;

import cz.rolling.moirai.model.common.ApproachType;
import cz.rolling.moirai.model.common.CharacterAttribute;
import cz.rolling.moirai.model.common.CharacterLabel;
import cz.rolling.moirai.validator.PreferenceFieldConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@PreferenceFieldConstraint(numberField = "numberOfPreferredCharacters", preferencesField = "ratingForPreferredCharacters")
@PreferenceFieldConstraint(numberField = "numberOfHatedCharacters", preferencesField = "ratingForHatedCharacters")
public class MainConfiguration {
    @NotNull
    private ApproachType approachType = null;

    @NotNull
    @Max(0)
    private Integer ratingForNotSpecifiedChar = -100;

    @NotNull
    @Max(0)
    private Integer ratingForGender = -1000;

    @NotNull
    @Max(0)
    private Integer ratingForLabel = -900;

    @NotNull
    @Min(1)
    @Max(15)
    private Integer numberOfPreferredCharacters = 3;

    @NotBlank
    private String ratingForPreferredCharacters = "5,3,1";

    @NotNull
    @Min(0)
    @Max(15)
    private Integer numberOfHatedCharacters = 0;

    private String ratingForHatedCharacters = "-10";

    @NotNull
    @Min(0)
    @Max(5)
    private Integer multiSelect = 0;

    @Valid
    @NotNull
    List<CharacterAttribute> attributeList = new ArrayList<>();

    @Valid
    @NotNull
    List<CharacterLabel> labelList = new ArrayList<>();
}
