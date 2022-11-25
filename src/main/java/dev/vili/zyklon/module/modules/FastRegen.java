package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.ModeSetting;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.lwjgl.glfw.GLFW;

public class FastRegen extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", this, "BruteForce", "BruteForce", "Potion");

    public FastRegen() {
        super("FastRegen", "Regenerates health faster. (Single player)", GLFW.GLFW_KEY_UNKNOWN, Category.PLAYER);
        this.addSettings(mode);
    }

    @Override
    public void onDisable() {
        if (mc.player != null) mc.player.removeStatusEffect(StatusEffects.REGENERATION);
    }

    @Subscribe
    public void onTick() {
        if (mode.is("BruteForce") && mc.player.getHealth() < mc.player.getMaxHealth())
            mc.player.setHealth(mc.player.getHealth() + 1);

        else if (mode.is("Potion") && mc.player.getHealth() < mc.player.getMaxHealth()) {
            mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 1, 3));
        }
    }
}
