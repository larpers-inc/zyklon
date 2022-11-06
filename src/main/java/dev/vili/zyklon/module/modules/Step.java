package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.NumberSetting;
import org.lwjgl.glfw.GLFW;
import dev.vili.zyklon.eventbus.Subscribe;

public class Step extends Module {
    public final NumberSetting height = new NumberSetting("Height", this, 2, 1, 10, 1);

    public Step() {
        super("Step", "Step up blocks.", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
        this.addSettings(height);
    }

    @Override
    public void onDisable() {
        if (mc.world != null) mc.player.stepHeight = .5f;
    }

    @Subscribe
    public void onTick(TickEvent event) {
        mc.player.stepHeight = (float) height.getValue();
    }
}