package com.github.lumin.mixins;

import com.github.lumin.events.JumpRotationEvent;
import com.github.lumin.managers.Managers;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import org.joml.Vector2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Redirect(method = "jumpFromGround", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getYRot()F"))
    private float redirectGetYRotInJumpFromGround(LivingEntity instance) {
        if (instance == Minecraft.getInstance().player) {
            JumpRotationEvent event = NeoForge.EVENT_BUS.post(new JumpRotationEvent(instance.getYRot()));
            return event.getYaw();
        }
        return instance.getYRot();
    }

    @Redirect(method = "tickHeadTurn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getYRot()F"))
    private float modifyHeadYaw(LivingEntity entity) {
        if (entity == Minecraft.getInstance().player) {
            Vector2f animationRotation = Managers.ROTATION.animationRotation;
            if (animationRotation != null) {
                return animationRotation.x;
            }
        }
        return entity.getYRot();
    }

}
