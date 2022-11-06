package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.module.Module;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.lwjgl.glfw.GLFW;
import dev.vili.zyklon.eventbus.Subscribe;

public class NoFall extends Module {
    public NoFall() {
        super("NoFall", "Prevents falldamage.", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player.fallDistance > 2f) mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
    }
}