package dev.lumin.client.gui.animation;

public enum Easing {

    LINEAR {
        @Override
        public float ease(float t) {
            return t;
        }
    },

    EASE_OUT {
        @Override
        public float ease(float t) {
            return 1 - (1 - t) * (1 - t);
        }
    },

    EASE_IN {
        @Override
        public float ease(float t) {
            return t * t;
        }
    },

    EASE_IN_OUT {
        @Override
        public float ease(float t) {
            return t < 0.5f ? 2 * t * t : 1 - (float) Math.pow(-2 * t + 2, 2) / 2;
        }
    },

    EASE_OUT_CUBIC {
        @Override
        public float ease(float t) {
            return 1 - (float) Math.pow(1 - t, 3);
        }
    },

    EASE_IN_CUBIC {
        @Override
        public float ease(float t) {
            return t * t * t;
        }
    },

    EASE_IN_OUT_CUBIC {
        @Override
        public float ease(float t) {
            return t < 0.5f ? 4 * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 3) / 2;
        }
    },

    EASE_OUT_QUART {
        @Override
        public float ease(float t) {
            return 1 - (float) Math.pow(1 - t, 4);
        }
    },

    EASE_OUT_EXPO {
        @Override
        public float ease(float t) {
            return t == 1 ? 1 : 1 - (float) Math.pow(2, -10 * t);
        }
    },

    EASE_OUT_BACK {
        @Override
        public float ease(float t) {
            float c1 = 1.70158f;
            float c3 = c1 + 1;
            return 1 + c3 * (float) Math.pow(t - 1, 3) + c1 * (float) Math.pow(t - 1, 2);
        }
    },

    EASE_OUT_ELASTIC {
        @Override
        public float ease(float t) {
            if (t == 0) return 0;
            if (t == 1) return 1;
            float p = 0.3f;
            float s = p / 4;
            return (float) Math.pow(2, -10 * t) * (float) Math.sin((t - s) * (2 * Math.PI) / p) + 1;
        }
    },

    EASE_OUT_BOUNCE {
        @Override
        public float ease(float t) {
            float n1 = 7.5625f;
            float d1 = 2.75f;
            if (t < 1 / d1) {
                return n1 * t * t;
            } else if (t < 2 / d1) {
                return n1 * (t -= 1.5f / d1) * t + 0.75f;
            } else if (t < 2.5 / d1) {
                return n1 * (t -= 2.25f / d1) * t + 0.9375f;
            } else {
                return n1 * (t -= 2.625f / d1) * t + 0.984375f;
            }
        }
    },

    SPRING {
        private static final float DAMPING = 0.5f;
        private static final float STIFFNESS = 100f;

        @Override
        public float ease(float t) {
            float omega = (float) Math.sqrt(STIFFNESS);
            float decay = (float) Math.exp(-DAMPING * omega * t);
            float oscillation = (float) Math.cos(omega * t);
            return 1 - decay * oscillation;
        }
    };

    public abstract float ease(float t);

    public float ease(float t, float b, float c, float d) {
        return b + c * ease(t / d);
    }
}
