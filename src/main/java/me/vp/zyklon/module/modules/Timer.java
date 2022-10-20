package me.vp.zyklon.module.modules;

import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.NumberSetting;
import org.lwjgl.glfw.GLFW;

public class Timer extends Module {
    public final NumberSetting speed = new NumberSetting("Speed", this, 10, 0.1, 20, 0.1);

    public Timer() {
        super("Timer", "Increases your game speed.", GLFW.GLFW_KEY_UNKNOWN, Category.MISC);
        this.addSettings(speed);
    }

    /* RenderTickCounterMixin */
}
