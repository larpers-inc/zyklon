package me.vp.zyklon.module.modules;

import me.vp.zyklon.module.Module;
import org.lwjgl.glfw.GLFW;

public class Clickgui extends Module {

    public Clickgui() {
        super("Clickgui", "Opens the gui.", GLFW.GLFW_KEY_RIGHT_SHIFT, Category.CLIENT);
    }

    @Override
    public void onEnable() {
        mc.setScreen(new me.vp.zyklon.clickgui.Clickgui());
        toggle();
    }
}