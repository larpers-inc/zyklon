package dev.vili.zyklon.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class RotationUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private float yaw, pitch;

    public RotationUtils(LivingEntity entity) {
        this.yaw = entity.getYaw();
        this.pitch = entity.getPitch();
    }

    public RotationUtils(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public void normalize() {
        this.yaw = MathHelper.wrapDegrees(yaw);
        this.pitch = MathHelper.wrapDegrees(pitch);
    }

    public void add(float yaw, float pitch) {
        this.yaw += yaw;
        this.pitch += pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public static RotationUtils fromPlayer() {
        return new RotationUtils(mc.player);
    }
}