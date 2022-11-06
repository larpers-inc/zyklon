package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.ClientMoveEvent;
import dev.vili.zyklon.event.events.PacketEvent;
import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.NumberSetting;
import dev.vili.zyklon.util.FakePlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Freecam extends Module {
    public final NumberSetting speed = new NumberSetting("Speed", this, 1, 0.1, 10, 0.1);

    private double[] playerPos;
    private float[] playerRot;
    private Entity riding;
    private boolean prevFlying;
    private float prevFlySpeed;

    public Freecam() {
        super("Freecam", "Allows you to move around while in spectator mode.", GLFW.GLFW_KEY_UNKNOWN, Category.RENDER);
        this.addSettings(speed);
    }

    @Override
    public void onEnable() {
        mc.chunkCullingEnabled = false;

        playerPos = new double[] { mc.player.getX(), mc.player.getY(), mc.player.getZ() };
        playerRot = new float[] { mc.player.getYaw(), mc.player.getPitch() };
        FakePlayerUtil.spawn();

        if (mc.player.getVehicle() != null) {
            riding = mc.player.getVehicle();
            mc.player.getVehicle().removeAllPassengers();
        }

        if (mc.player.isSprinting())
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));

        prevFlying = mc.player.getAbilities().flying;
        prevFlySpeed = mc.player.getAbilities().getFlySpeed();
    }

    @Override
    public void onDisable() {
        mc.chunkCullingEnabled = true;

        FakePlayerUtil.despawn();
        mc.player.noClip = false;
        mc.player.getAbilities().flying = prevFlying;
        mc.player.getAbilities().setFlySpeed(prevFlySpeed);

        mc.player.refreshPositionAndAngles(playerPos[0], playerPos[1], playerPos[2], playerRot[0], playerRot[1]);
        mc.player.setVelocity(Vec3d.ZERO);

        if (riding != null && mc.world.getEntityById(riding.getId()) != null) {
            mc.player.startRiding(riding);
        }
    }

    @Subscribe
    public void onPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof ClientCommandC2SPacket || event.getPacket() instanceof PlayerMoveC2SPacket) {
            event.setCancelled(true);
        }
    }

    @Subscribe
    public void onMove(ClientMoveEvent event) {
        mc.player.noClip = true;
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.world == null || mc.player == null) {
            this.setEnabled(false);
            return;
        }

        mc.player.setOnGround(false);
        mc.player.getAbilities().setFlySpeed((float) (speed.getValue() / 20));
        mc.player.getAbilities().flying = true;
        mc.player.setPose(EntityPose.STANDING);
    }

}
