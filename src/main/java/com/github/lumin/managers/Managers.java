package com.github.lumin.managers;

import com.github.lumin.managers.impl.ModuleManager;
import com.github.lumin.managers.impl.RotationManager;

public class Managers {

    public static ModuleManager MODULE;
    public static RotationManager ROTATION;

    public static void initManagers() {

        MODULE = new ModuleManager();
        ROTATION = new RotationManager();

    }

}
