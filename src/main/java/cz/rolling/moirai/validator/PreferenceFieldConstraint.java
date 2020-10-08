package cz.rolling.moirai.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {PreferenceFieldValidator.class})
@Repeatable(PreferenceFieldConstraint.List.class)
public @interface PreferenceFieldConstraint {
    String message() default "{custom.validation.constraints.preferences.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String numberField();

    String preferencesField();

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        PreferenceFieldConstraint[] value();
    }
}
