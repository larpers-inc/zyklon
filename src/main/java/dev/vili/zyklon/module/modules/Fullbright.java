package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.module.Module;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.lwjgl.glfw.GLFW;
import dev.vili.zyklon.eventbus.Subscribe;

public class Fullbright extends Module {
    public Fullbright() {
        super("Fullbright", "Puts your brightness to max.", GLFW.GLFW_KEY_UNKNOWN, Category.RENDER);
    }

    @Override
    public void onDisable() {
        mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);

        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Subscribe
    public void onTick(TickEvent event) {
        mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 500, 0));
    }

}