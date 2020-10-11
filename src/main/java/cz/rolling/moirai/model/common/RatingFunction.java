package cz.rolling.moirai.model.common;

public enum RatingFunction {
    CONSTANT("assignment-config.rating-function.constant"),
    LINEAR("assignment-config.rating-function.linear"),
    QUADRATIC("assignment-config.rating-function.quadratic"),
    CUBIC("assignment-config.rating-function.cubic"),
    EXPONENTIAL("assignment-config.rating-function.exponential");

    private final String key;

    RatingFunction(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
