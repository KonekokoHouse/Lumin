package com.github.lumin.modules.impl.client;

import com.github.lumin.modules.Category;
import com.github.lumin.modules.Module;

public class Test666 extends Module {
    public static Test666 INSTANCE = new Test666();

    public Test666() {
        super("Test666", "idk", Category.CLIENT);
    }
}