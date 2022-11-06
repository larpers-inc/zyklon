package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.module.Module;
import org.lwjgl.glfw.GLFW;

public class PortalGui extends Module {
    public PortalGui() {
        super("PortalGui", "Allows you to open guis while in portal.", GLFW.GLFW_KEY_UNKNOWN, Category.PLAYER);
    }

    /* ClientPlayerEntityMixin */
}
