package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.TickEvent;
import me.vp.zyklon.eventbus.Subscribe;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.NumberSetting;
import org.lwjgl.glfw.GLFW;

public class Twerk extends Module {
    public final NumberSetting speed = new NumberSetting("Speed", this, 5, 1, 10, 1);

    int i;
    public Twerk() {
        super("Twerk", "Twerks.", GLFW.GLFW_KEY_UNKNOWN, Category.MISC);
        this.addSettings(speed);
    }

    @Override
    public void onDisable() {
        mc.options.sneakKey.setPressed(false);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        i++;
        if (i < 10 - speed.getValue()) return;
        mc.options.sneakKey.setPressed(!mc.options.sneakKey.isPressed());
        i = -1;
    }
}
