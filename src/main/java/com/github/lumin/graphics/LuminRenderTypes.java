package com.github.lumin.graphics;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

public class LuminRenderTypes {

    public static RenderType RECTANGLE = RenderType.create(
            "lumin_rectangle", RenderSetup.builder(LuminRenderPipelines.RECTANGLE).createRenderSetup()
    );

}
