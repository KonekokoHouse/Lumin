package dev.lumin.client.modules.impl.client;

import dev.lumin.client.modules.Category;
import dev.lumin.client.modules.Module;
import dev.lumin.client.settings.impl.ModeSetting;
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
