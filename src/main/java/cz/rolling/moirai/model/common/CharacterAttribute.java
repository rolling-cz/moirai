package cz.rolling.moirai.model.common;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CharacterAttribute {

    @NotBlank
    private String name;

    @Min(1)
    private int min;

    @Min(1)
    private int max;

    private int rating;

    @NotNull
    private RatingFunction function;
}
