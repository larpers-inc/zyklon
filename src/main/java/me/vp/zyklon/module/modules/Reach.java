package me.vp.zyklon.module.modules;

import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.NumberSetting;
import org.lwjgl.glfw.GLFW;

public class Reach extends Module {
    public final NumberSetting range = new NumberSetting("Range", this, 3.5, 0.1, 10, 0.1);

    public Reach() {
        super("Reach", "Increases your reach.", GLFW.GLFW_KEY_UNKNOWN, Category.COMBAT);
        this.addSettings(range);
    }

    /* ClientPlayerInteractionMixin */
}
