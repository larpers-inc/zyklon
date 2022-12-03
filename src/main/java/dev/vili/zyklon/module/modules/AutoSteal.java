package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import dev.vili.zyklon.setting.settings.NumberSetting;
import org.lwjgl.glfw.GLFW;

public class AutoSteal extends Module {
    public final NumberSetting delay = new NumberSetting("Delay", this, 4, 1, 10, 1);
    public final BooleanSetting buttons = new BooleanSetting("Buttons", this, true);

    public AutoSteal() {
        super("AutoSteal", "Automatically steals from chests.", GLFW.GLFW_KEY_UNKNOWN, Category.PLAYER);
        this.addSettings(delay, buttons);
    }

    /* ContainerScreenMixin - Copied from Wurst client */
}
