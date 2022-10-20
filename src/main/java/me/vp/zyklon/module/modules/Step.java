package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.TickEvent;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.BooleanSetting;
import me.vp.zyklon.setting.settings.NumberSetting;
import net.minecraft.util.math.Box;
import org.lwjgl.glfw.GLFW;
import me.vp.zyklon.eventbus.Subscribe;

import java.util.ArrayDeque;
import java.util.Deque;

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