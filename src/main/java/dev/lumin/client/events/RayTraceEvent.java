package dev.lumin.client.events;

import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;

public class RayTraceEvent extends Event {
    private final Entity entity;
    private float yaw;
    private float pitch;

    public RayTraceEvent(Entity entity, float yaw, float pitch) {
        this.entity = entity;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
