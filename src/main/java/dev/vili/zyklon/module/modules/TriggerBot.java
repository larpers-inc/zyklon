package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import dev.vili.zyklon.setting.settings.NumberSetting;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

public class TriggerBot extends Module {
    public final NumberSetting delay = new NumberSetting("Delay", this, 10, 0.1, 20, 0.1);
    public final BooleanSetting deathDisable = new BooleanSetting("DeathDisable", this, true);

    int ticks;
    public TriggerBot() {
        super("TriggerBot", "Automatically attacks when you look at an enemy.", GLFW.GLFW_KEY_UNKNOWN, Category.COMBAT);
        this.addSettings(delay, deathDisable);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.world == null || mc.player == null) return;
        if (mc.player.getHealth() <= 0 && deathDisable.isEnabled()) this.disable();
        Optional<Entity> entity = DebugRenderer.getTargetedEntity(mc.player, 7);

        ticks++;
        if (ticks < delay.getValue()) return;
        ticks = 0;

        if (mc.crosshairTarget != null && entity.isPresent() && mc.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.ENTITY)
            mc.interactionManager.attackEntity(mc.player, entity.get());
    }

}
