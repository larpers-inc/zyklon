package me.vp.zyklon.module.modules;

import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.NumberSetting;
import org.lwjgl.glfw.GLFW;

public class CameraClip extends Module {
    public final NumberSetting range = new NumberSetting("Range", this, 10, 1, 50, 1);

    public CameraClip() {
        super("CameraClip", "Allows you to clip through blocks.", GLFW.GLFW_KEY_UNKNOWN, Category.RENDER);
        this.addSettings(range);
    }

    /* CameraMixin */
}
