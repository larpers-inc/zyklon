package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.TickEvent;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.BooleanSetting;
import me.vp.zyklon.setting.settings.NumberSetting;
import net.minecraft.util.math.Box;
import org.lwjgl.glfw.GLFW;
import org.quantumclient.energy.Subscribe;

import java.util.ArrayDeque;
import java.util.Deque;

public class Step extends Module {
    public final NumberSetting height = new NumberSetting("Height", this, 2, 1, 10, 1);
    public final BooleanSetting cooldown = new BooleanSetting("Cooldown", this, false);
    public final NumberSetting cooldownTime = new NumberSetting("CooldownTime", this, 0.1, 0.001, 1, 0.01);
    public Step() {
        super("Step", "Step up blocks.", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
        this.addSettings(height, cooldown, cooldownTime);
    }

    private int lastStep = 0;
    private Deque<Double> queue = new ArrayDeque<>();

    @Override
    public void onDisable() {
        if (mc.world != null) mc.player.stepHeight = .5f;
    }

    @Subscribe
    public void onTick(TickEvent event) {
        mc.player.stepHeight = (float) height.getValue();

        if (!mc.player.horizontalCollision) queue.clear();

        if (cooldown.isEnabled()) {
            if (!(mc.player.age < lastStep || mc.player.age >= lastStep + cooldownTime.getValue() * 20)) {
                return;
            }
        }
    }

    private boolean isTouchingWall(Box box) {
        return !mc.world.isSpaceEmpty(box.expand(0.01, 0, 0)) || !mc.world.isSpaceEmpty(box.expand(0, 0, 0.01));
    }

}