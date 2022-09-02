package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.PacketEvent;
import me.vp.zyklon.event.events.SwingHandEvent;
import me.vp.zyklon.eventbus.Subscribe;
import me.vp.zyklon.module.Module;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import org.lwjgl.glfw.GLFW;

public class NoSwing extends Module {

    public NoSwing() {
        super("NoSwing", "Makes your hand not swing.", GLFW.GLFW_KEY_UNKNOWN, Category.PLAYER);
    }

    @Subscribe
    public void onSwing(SwingHandEvent event) {
        event.setCancelled(true);
    }

    @Subscribe
    public void onPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof HandSwingC2SPacket) event.setCancelled(true);
    }
}