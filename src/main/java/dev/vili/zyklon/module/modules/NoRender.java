package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import org.lwjgl.glfw.GLFW;

public class NoRender extends Module {
    public final BooleanSetting armor = new BooleanSetting("Armor", this, false);
    public final BooleanSetting enchantmentTable = new BooleanSetting("ETableBooks", this, true);

    public NoRender() {
        super("NoRender", "Disables rendering of certain things", GLFW.GLFW_KEY_UNKNOWN, Category.RENDER);
        this.addSettings(armor, enchantmentTable);
    }
}
