package dev.lumin.client.mixins;

import dev.lumin.client.events.MotionEvent;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class MixinLocalPlayer {

    @Inject(method = "sendPosition", at = @At("HEAD"), cancellable = true)
    private void onPreSendPosition(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        MotionEvent event = NeoForge.EVENT_BUS.post(new MotionEvent.Pre(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), player.onGround()));
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

}
