package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.NumberSetting;
import org.lwjgl.glfw.GLFW;

public class CameraClip extends Module {
    public final NumberSetting range = new NumberSetting("Range", this, 10, 1, 50, 1);

    public CameraClip() {
        super("CameraClip", "Allows you to clip through blocks.", GLFW.GLFW_KEY_UNKNOWN, Category.RENDER);
        this.addSettings(range);
    }

    /* CameraMixin */
}
