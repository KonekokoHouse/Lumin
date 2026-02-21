package dev.lumin.client.managers.impl;

import dev.lumin.client.modules.Module;
import dev.lumin.client.modules.impl.client.ClickGui;
import dev.lumin.client.modules.impl.client.InterFace;
import dev.lumin.client.modules.impl.movement.Sprint;
import dev.lumin.client.modules.impl.visual.RenderTest;

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
