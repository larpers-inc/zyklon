package me.vp.zyklon.module.modules;

import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.BooleanSetting;
import org.lwjgl.glfw.GLFW;

public class Clickgui extends Module {
    public final BooleanSetting showDesc = new BooleanSetting("ShowDesc", this, true);

    public Clickgui() {
        super("Clickgui", "Opens the gui.", GLFW.GLFW_KEY_RIGHT_SHIFT, Category.CLIENT);
        this.addSettings(showDesc);
    }

    @Override
    public void onEnable() {
        mc.setScreen(new me.vp.zyklon.clickgui.Clickgui());
        toggle();
    }
}