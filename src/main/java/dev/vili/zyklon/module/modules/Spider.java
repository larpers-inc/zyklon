package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.NumberSetting;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import dev.vili.zyklon.eventbus.Subscribe;

public class Spider extends Module {
    public final NumberSetting speed = new NumberSetting("Speed", this, 0.2, 0.1, 2, 0.1);
    public Spider() {
        super("Spider", "Allows you to climb blocks.", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
        this.addSettings(speed);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player.horizontalCollision) {
            Vec3d velocity = mc.player.getVelocity();
            if (velocity.y >= 0.2)
                return;

            mc.player.setVelocity(velocity.x, 0.2 * speed.getValue(), velocity.z);
        }
    }
}