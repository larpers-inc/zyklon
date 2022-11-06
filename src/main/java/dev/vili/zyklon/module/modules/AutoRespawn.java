package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.PacketEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import org.lwjgl.glfw.GLFW;

public class AutoRespawn extends Module {

    public AutoRespawn() {
        super("AutoRespawn", "Automatically respawns.", GLFW.GLFW_KEY_UNKNOWN, Category.COMBAT);
    }

    @Subscribe
    public void onPacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof HealthUpdateS2CPacket packet) {
            if (packet.getHealth() > 0.0F) return;
            mc.player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN));
        }

    }
}