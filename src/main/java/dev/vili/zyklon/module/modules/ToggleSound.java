package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.module.Module;
import org.lwjgl.glfw.GLFW;

public class ToggleSound extends Module {

    public ToggleSound() {
        super("ToggleSound", "Plays a sound when a module is toggled.", GLFW.GLFW_KEY_UNKNOWN, Category.CLIENT);
    }


}

