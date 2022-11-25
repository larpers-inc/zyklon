package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.NumberSetting;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class ElytraBoost extends Module {
    public final NumberSetting speed = new NumberSetting("Speed", this, 0.3, 0.01, 0.15, 0.01);
    public NumberSetting maxBoost = new NumberSetting("MaxBoost", this, 2.5, 0, 5, 0.1);

    public ElytraBoost() {
        super("ElytraBoost", "Boosts your elytra.", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
        this.addSettings(speed, maxBoost);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        double currentVel = Math.abs(mc.player.getVelocity().x) + Math.abs(mc.player.getVelocity().y) + Math.abs(mc.player.getVelocity().z);
        float radianYaw = (float) Math.toRadians(mc.player.getYaw());
        float boost = (float) speed.getValue();

        if (mc.player.isFallFlying() && currentVel <= maxBoost.getValue()) {
            if (mc.options.backKey.isPressed()) {
                mc.player.addVelocity(MathHelper.sin(radianYaw) * boost, 0, MathHelper.cos(radianYaw) * -boost);
            } else if (mc.player.getPitch() > 0) {
                mc.player.addVelocity(MathHelper.sin(radianYaw) * -boost, 0, MathHelper.cos(radianYaw) * boost);
            }
        }
    }
}
