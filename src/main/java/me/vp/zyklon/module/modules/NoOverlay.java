package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.PlaySoundEvent;
import me.vp.zyklon.eventbus.Subscribe;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.BooleanSetting;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class NoOverlay extends Module {
    public final BooleanSetting vignette = new BooleanSetting("Vignette", this, true);
    public final BooleanSetting pumpkin = new BooleanSetting("Pumpkin", this, true);
    public final BooleanSetting powderedSnow = new BooleanSetting("PowderedSnow", this, true);
    public final BooleanSetting bossBar = new BooleanSetting("BossBar", this, true);
    public final BooleanSetting status = new BooleanSetting("Status", this, false);
    public final BooleanSetting toast = new BooleanSetting("Toast", this, false);

    public NoOverlay() {
        super("NoOverlay", "Dont render overlays.", GLFW.GLFW_KEY_UNKNOWN, Category.RENDER);
        this.addSettings(vignette, pumpkin, powderedSnow, bossBar, status, toast);
    }

    @Subscribe
    public void onSound(PlaySoundEvent event) {
        if (!toast.isEnabled() || !(event.getSoundInstance() instanceof PositionedSoundInstance instance)) return;

        Identifier identifier = instance.getId();

        if (identifier == SoundEvents.UI_TOAST_IN.getId() || identifier == SoundEvents.UI_TOAST_OUT.getId() || identifier == SoundEvents.UI_TOAST_CHALLENGE_COMPLETE.getId()) {
            event.setCancelled(true);
        }
    }
}

