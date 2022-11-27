package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import dev.vili.zyklon.util.ZLogger;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import org.lwjgl.glfw.GLFW;

public class AutoWalk extends Module {
    public final BooleanSetting stuckCheck = new BooleanSetting("StuckCheck", this, true);
    public AutoWalk() {
        super("AutoWalk", "Automatically walks forward.", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
        this.addSettings(stuckCheck);
    }

    int stuckTimer = 0;

    @Override
    public void onDisable() {
        mc.options.forwardKey.setPressed(false);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        // Check if player is stuck in a block
        if (mc.player.horizontalCollision && stuckCheck.isEnabled()) {
            mc.options.forwardKey.setPressed(false);
            stuckTimer++;
            if (stuckTimer >= 40) {
                ZLogger.error("You are stuck in a block!");
                mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_BELL, 1.0F));
                stuckTimer = 0;
            }
        }

        mc.options.forwardKey.setPressed(true);
    }
}

