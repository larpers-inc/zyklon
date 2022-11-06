package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import org.lwjgl.glfw.GLFW;

public class Clickgui extends Module {
    public final BooleanSetting showDesc = new BooleanSetting("ShowDesc", this, true);
    public final BooleanSetting snow = new BooleanSetting("SnowEffect", this, true);
    public final BooleanSetting waifu = new BooleanSetting("Waifu", this, true);

    public Clickgui() {
        super("Clickgui", "Opens the gui.", GLFW.GLFW_KEY_RIGHT_SHIFT, Category.CLIENT);
        this.addSettings(showDesc, snow, waifu);
    }

    @Override
    public void onEnable() {
        mc.setScreen(new dev.vili.zyklon.clickgui.Clickgui());
        toggle();
    }
}