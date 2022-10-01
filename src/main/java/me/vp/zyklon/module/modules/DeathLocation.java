package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.PacketEvent;
import me.vp.zyklon.eventbus.Subscribe;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.util.ZLogger;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import org.lwjgl.glfw.GLFW;

public class DeathLocation extends Module {

    public DeathLocation() {
        super("DeathLocation", "Send your death location in chat.", GLFW.GLFW_KEY_UNKNOWN, Category.MISC);
    }

    @Subscribe
    public void onPacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof HealthUpdateS2CPacket packet) {
            if (packet.getHealth() > 0.0F) return;
            int x = (int) mc.player.getPos().getX();
            int y = (int) mc.player.getPos().getY();
            int z = (int) mc.player.getPos().getZ();
            ZLogger.info(("You died at " + x + ", " + y + ", " + z));
        }
    }
}