package dev.lumin.client.graphics.skija;

import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.pipeline.RenderTarget;
import dev.lumin.client.graphics.skija.util.state.States;
import io.github.humbleui.skija.*;
import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

public class Skija {

    private static final Minecraft mc = Minecraft.getInstance();

    public static DirectContext context;

    private static BackendRenderTarget renderTarget;
    public static Surface surface;

    public static Canvas canvas;

    public static void initSkia() {
        if (context == null) {
            context = DirectContext.makeGL();
        }

        Surface face = surface;
        if (face != null) {
            face.close();
        }

        BackendRenderTarget render = renderTarget;
        if (render != null) {
            render.close();
        }

        renderTarget = BackendRenderTarget.makeGL(mc.getWindow().getWidth(), mc.getWindow().getHeight(), 0, 8, getMinecraftFBO(), FramebufferFormat.GR_GL_RGBA8);
        BackendRenderTarget target = renderTarget;
        surface = Surface.wrapBackendRenderTarget(context, target, SurfaceOrigin.BOTTOM_LEFT, ColorType.RGBA_8888, ColorSpace.getSRGB());
        canvas = surface.getCanvas();
    }

    public static int getMinecraftFBO() {
        RenderTarget renderTarget = mc.getMainRenderTarget();
        GlTexture colorTexture = (GlTexture) renderTarget.getColorTexture();
        return colorTexture != null ? colorTexture.glId() : 0;
    }

    public static void draw(Consumer<Canvas> drawingLogic) {
        States.INSTANCE.push();
        context.resetGLAll();
        canvas.save();

        float scaleFactor = mc.getWindow().getGuiScale();
        canvas.scale(scaleFactor, scaleFactor);

        drawingLogic.accept(canvas);
        canvas.restore();
        context.flush(surface);
        States.INSTANCE.pop();
    }

}
