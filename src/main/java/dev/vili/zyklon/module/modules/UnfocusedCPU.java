package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.module.Module;
import org.lwjgl.glfw.GLFW;

public class UnfocusedCPU extends Module {

    public UnfocusedCPU() {
        super("UnfocusedCPU", "Caps fps to 1 when unfocused.", GLFW.GLFW_KEY_UNKNOWN, Category.CLIENT);
    }

    /* MinecraftClientMixin.java */
}

