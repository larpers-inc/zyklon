package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import dev.vili.zyklon.util.ZLogger;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class AutoWalk extends Module {
    public final BooleanSetting stuckCheck = new BooleanSetting("StuckCheck", this, true);
    public AutoWalk() {
        super("AutoWalk", "Automatically walks forward.", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
        this.addSettings(stuckCheck);
    }

    @Override
    public void onDisable() {
        mc.options.forwardKey.setPressed(false);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        // Check if player is stuck in a block
        if (mc.player.horizontalCollision) {
            mc.options.forwardKey.setPressed(false);
            ZLogger.error("You are stuck in a block!");

            if (stuckCheck.isEnabled())
                ZLogger.trayMessage("Zyklon", "You are stuck in a block!", TrayIcon.MessageType.WARNING);
            return;
        }

        mc.options.forwardKey.setPressed(true);
    }
}

