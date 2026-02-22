package com.github.lumin.managers.impl;

import com.github.lumin.modules.Module;
import com.github.lumin.modules.impl.client.ClickGui;
import com.github.lumin.modules.impl.client.InterFace;
import com.github.lumin.modules.impl.movement.Sprint;
import com.github.lumin.modules.impl.visual.RenderTest;

import java.util.List;

public class ModuleManager {
    private List<Module> modules;

    public ModuleManager() {
        initModules();
    }

    private void initModules() {
        modules = List.of(
                // Combat

                // Movement
                Sprint.INSTANCE,

                // Visual
                RenderTest.INSTANCE,

                // Client
                ClickGui.INSTANCE,
                InterFace.INSTANCE
        );
    }

    public List<Module> getModules() {
        return modules;
    }

    public void onKeyPress(int keyCode) {
        for (final var module : modules) {
            if (module.keyBind == keyCode) {
                module.toggle();
            }
        }
    }

}
