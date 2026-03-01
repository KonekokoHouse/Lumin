package me.catrix.api.utils.DynamicIslandUtil;

import me.catrix.api.utils.math.Easing;
//import me.catrix.api.utils.math.FadeUtils;
import me.catrix.mod.modules.impl.client.ClickGui;

import java.awt.*;

public class DynamicIslandAnimationUtil {
    private boolean setup = false;
    public double from = 0;
    public double to = 0;
//    public final FadeUtils fadeUtils = new FadeUtils(0);
    private long lastDeltaTime = System.currentTimeMillis();

    private double smooth(double current, double target, double speed) {
        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastDeltaTime;
        lastDeltaTime = currentTime;

        speed = Math.abs(target - current) * speed;

        if (deltaTime < 1L) {
            deltaTime = 1L;
        }

        final double difference = current - target;
        final double smoothing = Math.max(speed * (deltaTime / 16.0), 0.15);

        if (difference > speed) {
            current = Math.max(current - smoothing, target);
        } else if (difference < -speed) {
            current = Math.min(current + smoothing, target);
        } else {
            current = target;
        }

        return current;
    }

    public static double getNotificationProgress(long startTime) {
        return Math.min((System.currentTimeMillis() - startTime) / 500f, 1.0f);
    }

    public static double getEasedNotificationProgress(long startTime) {
        return Easing.IOS.ease(getNotificationProgress(startTime));
    }

    public static Color getSwitchColor(long startTime, boolean isEnable) {
        double easedProgress = Easing.IOS.ease(Math.min((System.currentTimeMillis() - startTime) / 300f, 1.0f));
        if (isEnable) {
            return new Color(
                    (int) (0 + (68) * easedProgress),
                    (int) (0 + (248) * easedProgress),
                    (int) (0 + (68) * easedProgress),
                    (int) (100 + (202 - 100) * easedProgress)
            );
        } else {
            return new Color(
                    (int) (68 + (-68) * easedProgress),
                    (int) (248 + (-248) * easedProgress),
                    (int) (68 + (-68) * easedProgress),
                    (int) (202 + (100 - 202) * easedProgress)
            );
        }
    }

    public static float getSwitchPosition(long startTime, boolean isEnable, float startX, float endX) {
        double easedProgress = getEasedNotificationProgress(startTime);
        if (isEnable) {
            return startX + (float) ((endX - startX) * easedProgress);
        } else {
            return endX - (float) ((endX - startX) * easedProgress);
        }
    }

    public double get(double target) {
        long length = ClickGui.INSTANCE.animationTime.getValueInt();
        if (length == 0) return target;

        double speedFactor = 300.0 / Math.max(length, 1);
        double animationSpeed = 0.1 * speedFactor;

        if (!setup) {
            setup = true;
            from = target;
            to = target;
            return target;
        }

        if (target != to) {
            to = target;
        }

        from = smooth(from, to, animationSpeed);

        return from;
    }

    public double get(double target, long length, Easing ease) {
        if (length == 0) return target;

        double speedFactor = 300.0 / Math.max(length, 1);
        double animationSpeed = 0.1 * speedFactor;

        if (!setup) {
            setup = true;
            from = target;
            to = target;
            return target;
        }

        if (target != to) {
            to = target;
        }

        from = smooth(from, to, animationSpeed);

        return from;
    }

    public void reset() {
        setup = false;
        from = 0;
        to = 0;
        lastDeltaTime = System.currentTimeMillis();
    }

    public void setCurrent(double value) {
        from = value;
        to = value;
        setup = true;
        lastDeltaTime = System.currentTimeMillis();
    }

    public static float getNotificationAlphaProgress(long notificationStartTime) {
        long elapsed = System.currentTimeMillis() - notificationStartTime;
        if (elapsed < 300) {
            return (float) Easing.CubicOut.ease(elapsed / 300f);
        } else if (elapsed > 1700) {
            return (float) (1.0f - Easing.CubicIn.ease((elapsed - 1700) / 300f));
        } else {
            return 1.0f;
        }
    }
}
