package dev.lumin.client.gui.animation;

import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public final class AnimationUtil {

    private AnimationUtil() {
    }

    public static final long DURATION_FAST = 150L;
    public static final long DURATION_NORMAL = 200L;
    public static final long DURATION_SLOW = 300L;
    public static final long DURATION_VERY_SLOW = 500L;

    public static Animation fade(float from, float to) {
        return new Animation(from, to, DURATION_NORMAL, Easing.EASE_OUT);
    }

    public static Animation fade(float from, float to, long duration) {
        return new Animation(from, to, duration, Easing.EASE_OUT);
    }

    public static Animation slide(float from, float to) {
        return new Animation(from, to, DURATION_NORMAL, Easing.EASE_OUT_CUBIC);
    }

    public static Animation slide(float from, float to, long duration) {
        return new Animation(from, to, duration, Easing.EASE_OUT_CUBIC);
    }

    public static Animation scale(float from, float to) {
        return new Animation(from, to, DURATION_SLOW, Easing.EASE_OUT_BACK);
    }

    public static Animation scale(float from, float to, long duration) {
        return new Animation(from, to, duration, Easing.EASE_OUT_BACK);
    }

    public static Animation height(float from, float to) {
        return new Animation(from, to, DURATION_NORMAL, Easing.EASE_OUT_CUBIC);
    }

    public static Animation height(float from, float to, long duration) {
        return new Animation(from, to, duration, Easing.EASE_OUT_CUBIC);
    }

    public static Animation toggle(boolean state) {
        return new Animation(
                state ? 0f : 1f,
                state ? 1f : 0f,
                DURATION_FAST,
                Easing.EASE_OUT
        );
    }

    public static Animation spring(float from, float to) {
        return new Animation(from, to, DURATION_SLOW, Easing.SPRING);
    }

    public static Animation bounce(float from, float to) {
        return new Animation(from, to, DURATION_SLOW, Easing.EASE_OUT_BOUNCE);
    }

    public static float clamp(float value, float min, float max) {
        return Mth.clamp(value, min, max);
    }

    public static float lerp(float start, float end, float t) {
        return Mth.lerp(Mth.clamp(t, 0f, 1f), start, end);
    }

    public static float lerp(float start, float end, float t, Easing easing) {
        return Mth.lerp(easing.ease(Mth.clamp(t, 0f, 1f)), start, end);
    }

    public static int lerpColor(int startColor, int endColor, float t) {
        t = Mth.clamp(t, 0f, 1f);

        int a1 = (startColor >> 24) & 0xFF;
        int r1 = (startColor >> 16) & 0xFF;
        int g1 = (startColor >> 8) & 0xFF;
        int b1 = startColor & 0xFF;

        int a2 = (endColor >> 24) & 0xFF;
        int r2 = (endColor >> 16) & 0xFF;
        int g2 = (endColor >> 8) & 0xFF;
        int b2 = endColor & 0xFF;

        int a = Mth.lerpInt(t, a1, a2);
        int r = Mth.lerpInt(t, r1, r2);
        int g = Mth.lerpInt(t, g1, g2);
        int b = Mth.lerpInt(t, b1, b2);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int lerpColor(int startColor, int endColor, float t, Easing easing) {
        return lerpColor(startColor, endColor, easing.ease(Mth.clamp(t, 0f, 1f)));
    }

    public static float smoothstep(float edge0, float edge1, float x) {
        float t = Mth.clamp(Mth.inverseLerp(x, edge0, edge1), 0f, 1f);
        return (float) Mth.smoothstep(t);
    }

    public static float inverseLerp(float value, float start, float end) {
        return Mth.inverseLerp(value, start, end);
    }

    public static float map(float value, float inMin, float inMax, float outMin, float outMax) {
        return Mth.map(value, inMin, inMax, outMin, outMax);
    }

    public static float clampedMap(float value, float inMin, float inMax, float outMin, float outMax) {
        return Mth.clampedMap(value, inMin, inMax, outMin, outMax);
    }

    public static class AnimationGroup {
        private final List<Animation> animations = new ArrayList<>();
        private Runnable onComplete;

        public AnimationGroup add(Animation animation) {
            animations.add(animation);
            return this;
        }

        public AnimationGroup onComplete(Runnable callback) {
            this.onComplete = callback;
            return this;
        }

        public void startAll() {
            for (Animation anim : animations) {
                anim.start();
            }
        }

        public void updateAll() {
            boolean allFinished = true;
            for (Animation anim : animations) {
                anim.update();
                if (!anim.isFinished()) {
                    allFinished = false;
                }
            }
            if (allFinished && onComplete != null) {
                onComplete.run();
                onComplete = null;
            }
        }

        public boolean isAllFinished() {
            for (Animation anim : animations) {
                if (!anim.isFinished()) return false;
            }
            return true;
        }

        public void resetAll() {
            for (Animation anim : animations) {
                anim.reset();
            }
        }
    }

    public static AnimationGroup group() {
        return new AnimationGroup();
    }
}
