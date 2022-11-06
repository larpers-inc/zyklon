package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.module.Module;
import org.lwjgl.glfw.GLFW;

public class VanillaSpoof extends Module {
    public VanillaSpoof() {
        super("VanillaSpoof", "Make servers think that your on vanilla.", GLFW.GLFW_KEY_UNKNOWN, Category.CLIENT);
    }
}
