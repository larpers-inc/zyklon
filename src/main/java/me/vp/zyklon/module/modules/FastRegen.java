package me.vp.zyklon.module.modules;

import me.vp.zyklon.eventbus.Subscribe;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.ModeSetting;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.lwjgl.glfw.GLFW;

public class FastRegen extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", this, "BruteForce", "BruteForce", "Potion");

    public FastRegen() {
        super("FastRegen", "Regenerates health faster.", GLFW.GLFW_KEY_UNKNOWN, Category.PLAYER);
        this.addSettings(mode);
    }

    @Subscribe
    public void onTick() {
        if (mode.is("BruteForce") && mc.player.getHealth() < mc.player.getMaxHealth())
            mc.player.setHealth(mc.player.getHealth() + 1);

        else if (mode.is("Potion") && mc.player.getHealth() < mc.player.getMaxHealth()) {
            mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 1, 1));
        }
    }
}
