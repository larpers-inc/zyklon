package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

public class OneTapVehicle extends Module {
    public OneTapVehicle() {
        super("1TapVehicle", "Breaks vehicles that you are looking at in one hit.", GLFW.GLFW_KEY_UNKNOWN, Category.MISC);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.world == null || mc.player == null) return;

        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY && mc.options.attackKey.isPressed()) {
            Entity entity = ((EntityHitResult) mc.crosshairTarget).getEntity();
            if (entity instanceof BoatEntity || entity instanceof MinecartEntity) {
                // Send enough packets to kill the entity
                for (int i = 0; i < 20; i++) {
                    mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(entity, mc.player.isSneaking()));
                }
            }
        }
    }
}
