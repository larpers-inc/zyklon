package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.TickEvent;
import me.vp.zyklon.eventbus.Subscribe;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.BooleanSetting;
import org.lwjgl.glfw.GLFW;

public class Scaffold extends Module {
    public final BooleanSetting highlight = new BooleanSetting("Highlight", this, true);


    public Scaffold() {
        super("Scaffold", "Places blocks under you.", GLFW.GLFW_KEY_UNKNOWN, Category.PLAYER);
    }

    @Subscribe
    public void onTick(TickEvent event) {

    }
}

