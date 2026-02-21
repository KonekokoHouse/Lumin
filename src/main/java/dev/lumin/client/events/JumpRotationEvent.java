package dev.lumin.client.events;

import net.neoforged.bus.api.Event;

public class JumpRotationEvent extends Event {
    private float yaw;

    public JumpRotationEvent(float yaw) {
        this.yaw = yaw;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
