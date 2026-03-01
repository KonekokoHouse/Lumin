package com.github.lumin.modules.impl.client;

import com.github.lumin.modules.Category;
import com.github.lumin.modules.Module;
import com.github.lumin.settings.impl.ColorSetting;
import com.github.lumin.settings.impl.ModeSetting;

import java.awt.*;

public class Test2 extends Module {

    public static final Test2 INSTANCE = new Test2();

    public Test2() {
        super("测试模块2", "Another test module", Category.CLIENT);
    }

    public final ModeSetting modeSet = modeSetting("模式设置", "模式A", new String[]{"模式A", "模式B", "模式C"});
    public final ColorSetting colorSet = colorSetting("颜色设置", new Color(255, 0, 0));

}