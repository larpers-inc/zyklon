package dev.vili.zyklon.event.events;

import dev.vili.zyklon.event.Event;
import dev.vili.zyklon.util.RotationUtils;

public class PlayerPacketEvent extends Event {
    private final Mode mode;
    private float yaw;
    private float pitch;
    private boolean onGround;

    public PlayerPacketEvent(float yaw, float pitch, boolean onGround) {
        mode = Mode.PRE;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public PlayerPacketEvent() {
        mode = Mode.POST;
    }


    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setRotation(RotationUtils rotation) {
        this.yaw = rotation.getYaw();
        this.pitch = rotation.getPitch();
    }

    public RotationUtils getRotation() {
        return new RotationUtils(yaw, pitch);
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public Mode getMode() {
        return mode;
    }


    public enum Mode {
        PRE, POST
    }

}