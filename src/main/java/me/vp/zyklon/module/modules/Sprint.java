package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.TickEvent;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.ModeSetting;
import org.lwjgl.glfw.GLFW;
import org.quantumclient.energy.Subscribe;

public class Sprint extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", this, "Legit", "Legit", "Rage");
    public Sprint() {
        super("Sprint", "Makes the player automatically sprint.", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
        this.addSettings(mode);
    }

    @Override
    public void onDisable() {
        mc.player.setSprinting(false);
        mc.options.sprintKey.setPressed(false);
        super.onDisable();
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mode.is("Legit")) mc.options.sprintKey.setPressed(true);
        else if (mode.is("Rage")) mc.player.setSprinting(mc.player.forwardSpeed > 0 || mc.player.forwardSpeed < 0 || mc.player.sidewaysSpeed != 0);
    }
}