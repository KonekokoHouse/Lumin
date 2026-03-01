package com.github.lumin.modules.impl.client;

import com.github.lumin.modules.Category;
import com.github.lumin.modules.Module;

public class TestB extends Module {
    public static TestB INSTANCE = new TestB();

    public TestB() {
        super("TESTB", "idk", Category.CLIENT);
    }
}