package com.github.lumin.modules.impl.visual;

import com.github.lumin.modules.Category;
import com.github.lumin.modules.Module;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public class Nametags extends Module {

    public static final Nametags INSTANCE = new Nametags();

    public Nametags() {
        super("名牌显示", "看迪克", Category.VISUAL);
    }

    @SubscribeEvent
    private void onRenderGui(RenderGuiEvent.Post event) {


    }


    @SubscribeEvent
    private void onRenderAfterEntities(RenderLevelStageEvent.AfterEntities event) {


    }


}
