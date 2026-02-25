package com.github.lumin.modules.impl.client;

import com.github.lumin.modules.Category;
import com.github.lumin.modules.Module;

public class TestA extends Module {
    public static TestA INSTANCE = new TestA();

    public TestA() {
        super("TestA", "idk", "idk", "idk", Category.CLIENT);
    }
}
