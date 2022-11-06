package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.module.Module;
import org.lwjgl.glfw.GLFW;

public class GameWindowTitle extends Module {
    public GameWindowTitle() {
        super("GameWindowTitle", "Custom game window title.", GLFW.GLFW_KEY_UNKNOWN, Category.CLIENT);
    }

    /* MinecraftClientMixin.java */
}