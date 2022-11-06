package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import org.lwjgl.glfw.GLFW;

public class AutoWalk extends Module {
    public AutoWalk() {
        super("AutoWalk", "Automatically walks forward.", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
    }

    @Override
    public void onDisable() {
        mc.options.forwardKey.setPressed(false);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        mc.options.forwardKey.setPressed(true);
    }
}

