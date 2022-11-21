package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import org.lwjgl.glfw.GLFW;

public class BetterChat extends Module {
    public final BooleanSetting timestamps = new BooleanSetting("Timestamps", this, true);
    public final BooleanSetting mentionSound = new BooleanSetting("MentionSound", this, true);

    public BetterChat() {
        super("BetterChat", "Makes chat better.", GLFW.GLFW_KEY_UNKNOWN, Category.CLIENT);
        this.addSettings(timestamps, mentionSound);
    }

    /* ChatHudMixin.java */
}
