package cz.rolling.moirai.model.common;

public enum DeltaFunction {
    STANDARD("assignment-config.delta-function.standard") {
        @Override
        public int getDelta(int characterValue, int requiredValue, int min, int max) {
            return Math.abs(characterValue - requiredValue);
        }

        @Override
        public int getMaxDelta(int min, int max) {
            return max - min;
        }
    },
    MIDDLE_NEUTRAL("assignment-config.delta-function.middle-neutral") {
        @Override
        public int getDelta(int characterValue, int requiredValue, int min, int max) {
            int neutral = (max + min) / 2;
            if (requiredValue == neutral) {
                return 0;
            } else {
                return Math.abs(characterValue - requiredValue);
            }
        }

        @Override
        public int getMaxDelta(int min, int max) {
            return max - min;
        }
    },
    ONLY_BIGGER("assignment-config.delta-function.only-bigger") {
        @Override
        public int getDelta(int characterValue, int requiredValue, int min, int max) {
            if (characterValue > requiredValue) {
                return characterValue - requiredValue;
            } else {
                return 0;
            }
        }

        @Override
        public int getMaxDelta(int min, int max) {
            return max - min;
        }
    },
    ZERO_NEUTRAL("assignment-config.delta-function.zero-neutral") {
        @Override
        public int getDelta(int characterValue, int requiredValue, int min, int max) {
            if (requiredValue == 0) {
                return 0;
            } else {
                return Math.abs(characterValue - requiredValue);
            }
        }

        @Override
        public int getMaxDelta(int min, int max) {
            return (max - min) - 1;
        }
    };

    private final String key;

    DeltaFunction(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    abstract public int getDelta(int characterValue, int requiredValue, int min, int max);

    abstract public int getMaxDelta(int min, int max);
}
