package cz.rolling.moirai.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class PreferenceFieldValidator implements ConstraintValidator<PreferenceFieldConstraint, Object> {
    Logger logger = LoggerFactory.getLogger(PreferenceFieldValidator.class);
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
            logger.error("Constraint error", e);
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
