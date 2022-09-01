package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.PacketEvent;
import me.vp.zyklon.module.Module;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import org.lwjgl.glfw.GLFW;
import me.vp.zyklon.eventbus.Subscribe;

public class XCarry extends Module {
    public XCarry() {
        super("XCarry", "Allows you to carry extra 4 items.", GLFW.GLFW_KEY_UNKNOWN, Category.PLAYER);
    }
    private CloseHandledScreenC2SPacket lastPacket = null;

    @Override
    public void onDisable() {
        if (lastPacket != null) mc.player.networkHandler.sendPacket(lastPacket);
        lastPacket = null;
    }

    @Subscribe
    public void onPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof CloseHandledScreenC2SPacket packet) {
            if (packet.getSyncId() == mc.player.playerScreenHandler.syncId) {
                event.setCancelled(true);
                lastPacket = packet;
            }
        }
    }
}
