package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class ClickTp extends Module {

    public ClickTp() {
        super("ClickTp", "Teleports you to the block you click on.", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.world == null || mc.player == null) return;

        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK && mc.options.useKey.isPressed()) {
            Vec3d vec3d = mc.crosshairTarget.getPos();
            mc.player.updatePosition(vec3d.x, vec3d.y, vec3d.z);
        }
    }
}
