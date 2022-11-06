package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.module.Module;
import org.lwjgl.glfw.GLFW;

public class SafeWalk extends Module {
    public SafeWalk() {
        super("SafeWalk", "Prevents you from falling off blocks.", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
    }

    // Code in ClientPlayerMixin
}