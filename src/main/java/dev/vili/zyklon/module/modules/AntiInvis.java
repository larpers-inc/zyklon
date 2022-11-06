package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.module.Module;
import org.lwjgl.glfw.GLFW;

public class AntiInvis extends Module {

    public AntiInvis() {
        super("AntiInvis", "See invisible players.", GLFW.GLFW_KEY_UNKNOWN, Category.MISC);
    }

    
}