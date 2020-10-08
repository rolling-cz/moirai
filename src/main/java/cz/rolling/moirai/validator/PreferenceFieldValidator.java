package cz.rolling.moirai.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class PreferenceFieldValidator implements ConstraintValidator<PreferenceFieldConstraint, Object> {
    private String numberField;
    private String preferencesField;

    @Override
    public void initialize(PreferenceFieldConstraint constraint) {
        numberField = constraint.numberField();
        preferencesField = constraint.preferencesField();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        try {
            Integer numberFieldValue = (Integer) getFieldValue(object, numberField);
            String preferenceFieldValue = (String) getFieldValue(object, preferencesField);

            if (numberFieldValue == null || numberFieldValue < 1) {
                return true;
            }
            if (preferenceFieldValue == null) {
                return false;
            }
            String[] parts = preferenceFieldValue.split(",");

            return parts.length == numberFieldValue;
        } catch (Exception e) {
            // log error
            return false;
        }
    }

    private Object getFieldValue(Object object, String fieldName) throws Exception {
        Class<?> clazz = object.getClass();
        Field passwordField = clazz.getDeclaredField(fieldName);
        passwordField.setAccessible(true);
        return passwordField.get(object);
    }
}
