package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.NumberSetting;
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
