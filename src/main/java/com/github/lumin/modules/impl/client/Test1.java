package com.github.lumin.modules.impl.client;

import com.github.lumin.modules.Category;
import com.github.lumin.modules.Module;
import com.github.lumin.settings.impl.BoolSetting;
import com.github.lumin.settings.impl.DoubleSetting;

public class Test1 extends Module {

    public static final Test1 INSTANCE = new Test1();

    public Test1() {
        super("测试模块1", "Just a test module", Category.CLIENT);
    }

    public final BoolSetting boolSet = boolSetting("布尔设置", true);
    public final DoubleSetting doubleSet = doubleSetting("小数设置", 5.0, 0.0, 10.0, 0.1);

}