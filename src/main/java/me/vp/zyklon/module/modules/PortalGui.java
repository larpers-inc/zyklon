package me.vp.zyklon.module.modules;

import me.vp.zyklon.module.Module;
import org.lwjgl.glfw.GLFW;

public class PortalGui extends Module {
    public PortalGui() {
        super("PortalGui", "Allows you to open guis while in portal.", GLFW.GLFW_KEY_UNKNOWN, Category.PLAYER);
    }
}
