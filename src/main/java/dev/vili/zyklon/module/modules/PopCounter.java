package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.PacketEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.util.ZLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import org.lwjgl.glfw.GLFW;

public class PopCounter extends Module {

    public PopCounter() {
        super("PopCounter", "Counts how many times a player has popped a totem.", GLFW.GLFW_KEY_UNKNOWN, Category.COMBAT);
    }

    @Subscribe
    public void onPacket(PacketEvent.Receive event) {
        if (!(event.getPacket() instanceof EntityStatusS2CPacket packet )) return;
        if (packet.getStatus() != 35) return;
        Entity entity = packet.getEntity(mc.world);

        if (!(entity instanceof PlayerEntity)) return;

        ZLogger.info(entity.getEntityName() + " has popped a totem!");
    }
}
