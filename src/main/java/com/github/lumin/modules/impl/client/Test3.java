package com.github.lumin.modules.impl.client;

import com.github.lumin.modules.Category;
import com.github.lumin.modules.Module;
import com.github.lumin.settings.impl.IntSetting;
import com.github.lumin.settings.impl.StringSetting;

public class Test3 extends Module {

    public static final Test3 INSTANCE = new Test3();

    public Test3() {
        super("测试模块3", "Yet another test module", Category.CLIENT);
    }

    public final IntSetting intSet = intSetting("整数设置", 5, 1, 10, 1);
    public final StringSetting stringSet = stringSetting("字符串设置", "默认");

}