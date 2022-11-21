package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.NumberSetting;
import org.lwjgl.glfw.GLFW;

public class Reach extends Module {
    public final NumberSetting range = new NumberSetting("Range", this, 3.5, 0.1, 30, 0.1);

    public Reach() {
        super("Reach", "Increases your reach.", GLFW.GLFW_KEY_UNKNOWN, Category.COMBAT);
        this.addSettings(range);
    }

    /* ClientPlayerInteractionMixin */
}
