package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.NumberSetting;
import org.lwjgl.glfw.GLFW;

public class HighJump extends Module {
    public final NumberSetting strength = new NumberSetting("Strength", this, 1, 0.01, 3, 0.01);

    public HighJump() {
        super("HighJump", "Makes you jump higher", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
        this.addSettings(strength);
    }

    /* ClientPlayerEntityMixin */
}
