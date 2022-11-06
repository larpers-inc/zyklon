package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import org.lwjgl.glfw.GLFW;

public class Scaffold extends Module {
    public final BooleanSetting highlight = new BooleanSetting("Highlight", this, true);


    public Scaffold() {
        super("Scaffold", "Places blocks under you.", GLFW.GLFW_KEY_UNKNOWN, Category.PLAYER);
        this.addSettings(highlight);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.world == null || mc.player == null) return;
    }
}

