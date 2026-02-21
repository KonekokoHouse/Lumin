package dev.lumin.client.graphics.skija.font;

import io.github.humbleui.skija.Font;

public class FontLoader {

    public static Font light(float size) {
        return FontManager.font("PingFangSC-Light.otf", size);
    }

    public static Font medium(float size) {
        return FontManager.font("PingFangSC-Medium.otf", size);
    }

    public static Font regular(float size) {
        return FontManager.font("PingFangSC-Regular.otf", size);
    }

    public static Font semibold(float size) {
        return FontManager.font("PingFangSC-Semibold.otf", size);
    }

}
