package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.PacketEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.util.ZLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import org.lwjgl.glfw.GLFW;

public class PopCounter extends Module {

    public PopCounter() {
        super("PopCounter", "Counts how many times a player has popped a totem.", GLFW.GLFW_KEY_UNKNOWN, Category.COMBAT);
    }

    int count;

    @Subscribe
    public void onPacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof EntityStatusS2CPacket packet) {
            Entity entity = packet.getEntity(mc.world);
            if (entity != null && packet.getStatus() == EntityStatuses.USE_TOTEM_OF_UNDYING) {
                if (entity instanceof PlayerEntity) {
                    count++;
                    ZLogger.info(entity.getEntityName() + " has popped " + count + " totems.");
                }
            }
        }
    }
}
