package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.TickEvent;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import me.vp.zyklon.eventbus.Subscribe;

public class Fly extends Module {
    public final NumberSetting speed = new NumberSetting("Speed", this, 2, 1, 10, 1);
    public Fly() {
        super("Fly", "Allows you to fly.", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
        this.addSettings(speed);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        int flySpeed = (int) speed.getValue();

        if (mc.player.isRiding()) {
            Entity riding = mc.player.getRootVehicle();
            Vec3d velocity = riding.getVelocity();
            double motionY = mc.options.jumpKey.isPressed() ? 0.3 : 0;
            riding.setVelocity(velocity.x, motionY, velocity.z);
        } else {
            if (mc.options.sprintKey.isPressed()) {
                flySpeed *= 1.5;
            }
            mc.player.setVelocity(new Vec3d(0, 0, 0));
            mc.player.setMovementSpeed(flySpeed * 0.2f);
            mc.player.airStrafingSpeed = flySpeed * 0.2f;

            Vec3d vec = new Vec3d(0, 0, 0);
            if (mc.options.jumpKey.isPressed()) {
                vec = new Vec3d(0, flySpeed * 0.2f, 0);
            }
            if (mc.options.sneakKey.isPressed()) {
                vec = new Vec3d(0, -flySpeed * 0.2f, 0);
            }
            mc.player.setVelocity(vec);
        }
    }
}