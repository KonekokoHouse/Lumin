package com.github.lumin.graphics;

import net.minecraft.util.Mth;

public final class AnimationUtils {

    public static float linear(float start, float end, float progress) {
        return start + (end - start) * progress;
    }

    public static float easeInQuad(float start, float end, float progress) {
        float t = progress * progress;
        return start + (end - start) * t;
    }

    public static float easeOutQuad(float start, float end, float progress) {
        float t = 1 - (1 - progress) * (1 - progress);
        return start + (end - start) * t;
    }

    public static float easeInOutQuad(float start, float end, float progress) {
        float t = progress < 0.5f
                ? 2 * progress * progress
                : 1 - (float) Math.pow(-2 * progress + 2, 2) / 2;
        return start + (end - start) * t;
    }

    public static float easeInCubic(float start, float end, float progress) {
        float t = progress * progress * progress;
        return start + (end - start) * t;
    }

    public static float easeOutCubic(float start, float end, float progress) {
        float t = 1 - (float) Math.pow(1 - progress, 3);
        return start + (end - start) * t;
    }

    public static float easeInOutCubic(float start, float end, float progress) {
        float t = progress < 0.5f
                ? 4 * progress * progress * progress
                : 1 - (float) Math.pow(-2 * progress + 2, 3) / 2;
        return start + (end - start) * t;
    }

    public static float easeInExpo(float start, float end, float progress) {
        if (progress == 0) return start;
        float t = (float) Math.pow(2, 10 * progress - 10);
        return start + (end - start) * t;
    }

    public static float easeOutExpo(float start, float end, float progress) {
        if (progress == 1) return end;
        float t = 1 - (float) Math.pow(2, -10 * progress);
        return start + (end - start) * t;
    }

    public static float easeInOutExpo(float start, float end, float progress) {
        if (progress == 0) return start;
        if (progress == 1) return end;
        float t = progress < 0.5f
                ? (float) Math.pow(2, 20 * progress - 10) / 2
                : (2 - (float) Math.pow(2, -20 * progress + 10)) / 2;
        return start + (end - start) * t;
    }

    public static float easeOutElastic(float start, float end, float progress) {
        float c4 = (2 * (float) Math.PI) / 3;
        if (progress == 0) return start;
        if (progress == 1) return end;
        float t = (float) Math.pow(2, -10 * progress) * (float) Math.sin((progress * 10 - 0.75) * c4) + 1;
        return start + (end - start) * t;
    }

    public static float easeOutBack(float start, float end, float progress) {
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        float t = 1 + c3 * (float) Math.pow(progress - 1, 3) + c1 * (float) Math.pow(progress - 1, 2);
        return start + (end - start) * t;
    }

    public static float easeOutBounce(float start, float end, float progress) {
        float n11 = 7.5625f;
        float d13 = 2.75f;
        float t;

        if (progress < 1 / d13) {
            t = n11 * progress * progress;
        } else if (progress < 2 / d13) {
            float t2 = progress - 1.5f / d13;
            t = n11 * t2 * t2 + 0.75f;
        } else if (progress < 2.5 / d13) {
            float t2 = progress - 2.25f / d13;
            t = n11 * t2 * t2 + 0.9375f;
        } else {
            float t2 = progress - 2.625f / d13;
            t = n11 * t2 * t2 + 0.984375f;
        }

        return start + (end - start) * t;
    }

    public static float normalize(float value, float min, float max) {
        return Mth.clamp((value - min) / (max - min), 0, 1);
    }

    public static float smoothStep(float edge0, float edge1, float x) {
        float t = Mth.clamp((x - edge0) / (edge1 - edge0), 0, 1);
        return t * t * (3 - 2 * t);
    }

    public static float smootherStep(float edge0, float edge1, float x) {
        float t = Mth.clamp((x - edge0) / (edge1 - edge0), 0, 1);
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    public static class AnimatedValue {
        private float currentValue;
        private float targetValue;
        private float animationSpeed;
        private long lastUpdateTime;

        public AnimatedValue(float initialValue, float animationSpeed) {
            this.currentValue = initialValue;
            this.targetValue = initialValue;
            this.animationSpeed = animationSpeed;
            this.lastUpdateTime = System.currentTimeMillis();
        }

        public void setTarget(float target) {
            this.targetValue = target;
        }

        public void setImmediate(float value) {
            this.currentValue = value;
            this.targetValue = value;
        }

        public float update() {
            long currentTime = System.currentTimeMillis();
            float deltaTime = (currentTime - lastUpdateTime) / 1000f;
            lastUpdateTime = currentTime;

            if (Math.abs(currentValue - targetValue) < 0.001f) {
                currentValue = targetValue;
            } else {
                float diff = targetValue - currentValue;
                float change = diff * animationSpeed * deltaTime * 60;

                if (Math.abs(change) > Math.abs(diff)) {
                    currentValue = targetValue;
                } else {
                    currentValue += change;
                }
            }

            return currentValue;
        }

        public float getCurrentValue() {
            return currentValue;
        }

        public float getTargetValue() {
            return targetValue;
        }

        public boolean isAnimating() {
            return Math.abs(currentValue - targetValue) > 0.001f;
        }

        public void setAnimationSpeed(float speed) {
            this.animationSpeed = speed;
        }
    }

    public static class SmoothAnimation {
        private float startValue;
        private float endValue;
        private long startTime;
        private long duration;
        private EasingType easingType;
        private boolean completed;

        public SmoothAnimation(float startValue, float endValue, long durationMs, EasingType easingType) {
            this.startValue = startValue;
            this.endValue = endValue;
            this.duration = durationMs;
            this.easingType = easingType;
            this.startTime = System.currentTimeMillis();
            this.completed = false;
        }

        public float getCurrentValue() {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Mth.clamp((float) elapsed / duration, 0, 1);

            if (progress >= 1) {
                completed = true;
                return endValue;
            }

            float easedProgress = applyEasing(progress);
            return Mth.lerp(easedProgress, startValue, endValue);
        }

        private float applyEasing(float t) {
            return switch (easingType) {
                case LINEAR -> t;
                case EASE_IN_QUAD -> t * t;
                case EASE_OUT_QUAD -> 1 - (1 - t) * (1 - t);
                case EASE_IN_OUT_QUAD -> t < 0.5f ? 2 * t * t : 1 - (float) Math.pow(-2 * t + 2, 2) / 2;
                case EASE_IN_CUBIC -> t * t * t;
                case EASE_OUT_CUBIC -> 1 - (float) Math.pow(1 - t, 3);
                case EASE_IN_OUT_CUBIC -> t < 0.5f ? 4 * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 3) / 2;
                case EASE_OUT_EXPO -> t == 1 ? 1 : 1 - (float) Math.pow(2, -10 * t);
                case EASE_OUT_BACK -> {
                    float c1 = 1.70158f;
                    float c3 = c1 + 1;
                    yield 1 + c3 * (float) Math.pow(t - 1, 3) + c1 * (float) Math.pow(t - 1, 2);
                }
                case EASE_OUT_ELASTIC -> {
                    if (t == 0) yield 0;
                    if (t == 1) yield 1;
                    float c4 = (2 * (float) Math.PI) / 3;
                    yield (float) Math.pow(2, -10 * t) * (float) Math.sin((t * 10 - 0.75) * c4) + 1;
                }
            };
        }

        public boolean isCompleted() {
            return completed;
        }

        public void reset(float startValue, float endValue) {
            this.startValue = startValue;
            this.endValue = endValue;
            this.startTime = System.currentTimeMillis();
            this.completed = false;
        }

        public float getProgress() {
            long elapsed = System.currentTimeMillis() - startTime;
            return Mth.clamp((float) elapsed / duration, 0, 1);
        }
    }

    public enum EasingType {
        LINEAR,
        EASE_IN_QUAD,
        EASE_OUT_QUAD,
        EASE_IN_OUT_QUAD,
        EASE_IN_CUBIC,
        EASE_OUT_CUBIC,
        EASE_IN_OUT_CUBIC,
        EASE_OUT_EXPO,
        EASE_OUT_BACK,
        EASE_OUT_ELASTIC
    }
}
