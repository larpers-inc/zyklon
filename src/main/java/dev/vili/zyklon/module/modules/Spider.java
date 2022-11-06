package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.module.Module;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import dev.vili.zyklon.eventbus.Subscribe;

public class Spider extends Module {

    public Spider() {
        super("Spider", "Allows you to climb blocks.", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player.horizontalCollision) {
            Vec3d velocity = mc.player.getVelocity();
            if (velocity.y >= 0.2)
                return;

            mc.player.setVelocity(velocity.x, 0.2, velocity.z);
        }
    }
}