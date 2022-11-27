package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.lwjgl.glfw.GLFW;

public class FastRegen extends Module {
    public FastRegen() {
        super("FastRegen", "Tries to regenerate health faster.", GLFW.GLFW_KEY_UNKNOWN, Category.PLAYER);
    }

    @Override
    public void onDisable() {
        if (mc.player != null) mc.player.removeStatusEffect(StatusEffects.REGENERATION);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player.getHealth() < mc.player.getMaxHealth())
            mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 1, 3));
    }
}
