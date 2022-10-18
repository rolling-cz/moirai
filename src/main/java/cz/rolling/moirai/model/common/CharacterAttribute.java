package cz.rolling.moirai.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CharacterAttribute {

    @NotBlank
    private String name;

    @NotNull
    @Min(1)
    private Integer min;

    @NotNull
    @Min(1)
    private Integer max;

    @NotNull
    @Max(-1)
    private Integer rating;

    @NotNull
    private RatingFunction ratingFunction;

    @NotNull
    private DeltaFunction deltaFunction;
}
