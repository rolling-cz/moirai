package cz.rolling.moirai.model.common;

public enum RatingFunction {
    CONSTANT("assignment-config.rating-function.constant") {
        @Override
        public double getRating(int constant, int delta) {
            return constant;
        }
    },
    LINEAR("assignment-config.rating-function.linear") {
        @Override
        public double getRating(int constant, int delta) {
            return constant * delta;
        }
    },
    QUADRATIC("assignment-config.rating-function.quadratic") {
        @Override
        public double getRating(int constant, int delta) {
            return constant * Math.pow(delta, 2);
        }
    },
    CUBIC("assignment-config.rating-function.cubic") {
        @Override
        public double getRating(int constant, int delta) {
            return constant * Math.pow(delta, 3);
        }
    },
    EXPONENTIAL("assignment-config.rating-function.exponential") {
        @Override
        public double getRating(int constant, int delta) {
            return constant * Math.pow(2, delta);
        }
    };

    private final String key;

    RatingFunction(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    abstract public double getRating(int constant, int delta);
}
