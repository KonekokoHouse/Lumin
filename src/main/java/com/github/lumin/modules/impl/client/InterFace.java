package com.github.lumin.modules.impl.client;

import com.github.lumin.modules.Category;
import com.github.lumin.modules.Module;
import com.github.lumin.settings.impl.ModeSetting;
import io.github.humbleui.skija.FilterBlurMode;

public class InterFace extends Module {
    public static InterFace INSTANCE = new InterFace();

    public InterFace() {
        super("Interface", "界面", Category.CLIENT);
    }

    public ModeSetting filterBlurMode = modeSetting("FilterBlur", "滤镜模糊", "NORMAL", new String[]{"NORMAL", "SOLID", "OUTER", "INNER"});

    public FilterBlurMode filterBlurMode() {
        return switch (filterBlurMode.getValue()) {
            case "SOLID" -> FilterBlurMode.SOLID;
            case "OUTER" -> FilterBlurMode.OUTER;
            case "INNER" -> FilterBlurMode.INNER;
            default -> FilterBlurMode.NORMAL;
        };
    }
}
