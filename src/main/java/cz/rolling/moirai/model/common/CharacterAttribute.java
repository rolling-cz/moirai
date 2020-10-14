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

    @NotNull
    @Min(1)
    private Integer min;

    @NotNull
    @Min(1)
    private Integer max;

    @NotNull
    private Integer rating;

    @NotNull
    private RatingFunction function;
}
