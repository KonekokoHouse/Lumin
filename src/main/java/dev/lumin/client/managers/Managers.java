package dev.lumin.client.managers;

import dev.lumin.client.managers.impl.ModuleManager;
import dev.lumin.client.managers.impl.RotationManager;

public class Managers {

    public static ModuleManager MODULE;
    public static RotationManager ROTATION;

    public static void initManagers() {

        MODULE = new ModuleManager();
        ROTATION = new RotationManager();

    }

}
