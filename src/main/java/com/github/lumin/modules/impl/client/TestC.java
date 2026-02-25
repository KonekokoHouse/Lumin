package com.github.lumin.modules.impl.client;

import com.github.lumin.modules.Category;
import com.github.lumin.modules.Module;

public class TestC extends Module {
    public static TestC INSTANCE = new TestC();

    public TestC() {
        super("TestC", "idk", "idk", "idk", Category.CLIENT);
    }
}
