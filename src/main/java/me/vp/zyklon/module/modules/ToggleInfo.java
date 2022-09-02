package me.vp.zyklon.module.modules;

import me.vp.zyklon.module.Module;
import org.lwjgl.glfw.GLFW;

public class ToggleInfo extends Module {

    public ToggleInfo() {
        super("ToggleInfo", "Tells you when you enable/disable a module.", GLFW.GLFW_KEY_UNKNOWN, Category.CLIENT);
    }
}