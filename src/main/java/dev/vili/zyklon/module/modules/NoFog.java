package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.module.Module;
import org.lwjgl.glfw.GLFW;

public class NoFog extends Module {

    public NoFog() {
        super("NoFog", "Disables fog", GLFW.GLFW_KEY_UNKNOWN, Category.RENDER);
    }
}
