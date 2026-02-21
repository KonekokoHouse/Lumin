package dev.lumin.client.gui.theme;

import java.awt.*;

public final class Theme {

    private Theme() {
    }

    public static final int BACKGROUND = 0xFF1A1A1A;
    public static final int PANEL_BG = 0xD91C1C1E;
    public static final int DIVIDER = 0x1AFFFFFF;
    public static final int PRIMARY = 0xFF0A84FF;
    public static final int SECONDARY = 0xFF5E5CE6;
    public static final int TEXT_TITLE = 0xFFFFFFFF;
    public static final int TEXT_DESCRIPTION = 0x99FFFFFF;
    public static final int TEXT_DISABLED = 0x4DFFFFFF;
    public static final int HOVER_BG = 0x0DFFFFFF;
    public static final int ACTIVE_BG = 0x1AFFFFFF;
    public static final int BORDER = 0x1AFFFFFF;
    public static final int SUCCESS = 0xFF30D158;
    public static final int WARNING = 0xFFFFD60A;
    public static final int ERROR = 0xFFFF453A;

    public static Color getColor(int rgba) {
        return new Color(rgba, true);
    }

    public static int getRGBA(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int getRGBA(int rgb, float alpha) {
        int a = (int) (alpha * 255);
        return (a << 24) | (rgb & 0x00FFFFFF);
    }

    public static int withAlpha(int rgba, float alpha) {
        int a = Math.max(0, Math.min(255, (int) (alpha * 255)));
        return (a << 24) | (rgba & 0x00FFFFFF);
    }

    public static int interpolate(int color1, int color2, float factor) {
        factor = Math.max(0, Math.min(1, factor));

        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int a = (int) (a1 + (a2 - a1) * factor);
        int r = (int) (r1 + (r2 - r1) * factor);
        int g = (int) (g1 + (g2 - g1) * factor);
        int b = (int) (b1 + (b2 - b1) * factor);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int getDarker(int rgba, float factor) {
        int a = (rgba >> 24) & 0xFF;
        int r = (int) (((rgba >> 16) & 0xFF) * factor);
        int g = (int) (((rgba >> 8) & 0xFF) * factor);
        int b = (int) ((rgba & 0xFF) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int getLighter(int rgba, float factor) {
        int a = (rgba >> 24) & 0xFF;
        int r = Math.min(255, (int) (((rgba >> 16) & 0xFF) + (255 - ((rgba >> 16) & 0xFF)) * factor));
        int g = Math.min(255, (int) (((rgba >> 8) & 0xFF) + (255 - ((rgba >> 8) & 0xFF)) * factor));
        int b = Math.min(255, (int) ((rgba & 0xFF) + (255 - (rgba & 0xFF)) * factor));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static final class Radius {
        public static final float SMALL = 6f;
        public static final float MEDIUM = 10f;
        public static final float LARGE = 14f;
        public static final float XLARGE = 20f;
        public static final float ROUND = 100f;

        private Radius() {
        }
    }

    public static final class Spacing {
        public static final float XS = 4f;
        public static final float SM = 8f;
        public static final float MD = 12f;
        public static final float LG = 16f;
        public static final float XL = 24f;
        public static final float XXL = 32f;

        private Spacing() {
        }
    }

    public static final class Dimension {
        public static final float WINDOW_WIDTH = 500f;
        public static final float WINDOW_HEIGHT = 350f;
        public static final float NAVBAR_WIDTH = 80f;
        public static final float TITLEBAR_HEIGHT = 36f;
        public static final float MODULE_HEIGHT = 32f;
        public static final float SETTING_HEIGHT = 28f;
        public static final float COMPONENT_HEIGHT = 24f;

        private Dimension() {
        }
    }

    public static final class FontSize {
        public static final float TITLE = 18f;
        public static final float SUBTITLE = 14f;
        public static final float BODY = 12f;
        public static final float SMALL = 10f;
        public static final float TINY = 8f;

        private FontSize() {
        }
    }
}
