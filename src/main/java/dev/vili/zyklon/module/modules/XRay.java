package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.module.Module;

import org.lwjgl.glfw.GLFW;

public class XRay extends Module {

    public XRay() {
        super("XRay", "Allows you to see through blocks.", GLFW.GLFW_KEY_UNKNOWN, Category.RENDER);
    }

    @Override
    public void onDisable() {
        mc.worldRenderer.reload();
        Zyklon.INSTANCE.xrayManager.save();
    }

    @Override
    public void onEnable() {
        mc.worldRenderer.reload();
        Zyklon.INSTANCE.xrayManager.save();
    }

}

