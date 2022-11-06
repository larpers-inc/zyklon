package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.ModeSetting;
import dev.vili.zyklon.setting.settings.NumberSetting;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Speed extends Module {
    public final NumberSetting speed = new NumberSetting("Speed", this, 1, 0.1, 10, 0.1);
    public final NumberSetting strafeSpeed = new NumberSetting("StrafeSpeed", this, 0.25, 0.15, 0.55, 0.01);
    public final ModeSetting mode = new ModeSetting("Mode", this, "Vanilla", "Vanilla", "Strafe", "Bhop");

    private boolean jumping;
    public Speed() {
        super("Speed", "Makes you go faster.", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
        this.addSettings(speed, strafeSpeed, mode);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player.isSneaking()) return;

        if (mode.getMode().equalsIgnoreCase("Vanilla")) {
            if (mc.options.jumpKey.isPressed() || mc.player.fallDistance > 0.25)
                return;

            double speeds = 0.85 + speed.getValue() / 30;
            if (mc.player.forwardSpeed != 0 || mc.player.sidewaysSpeed != 0) {
                if (mc.player.isOnGround())
                    mc.player.setVelocity(mc.player.getVelocity().x * speeds, 0, mc.player.getVelocity().z * speeds);
            }
        }
        else if (mode.getMode().equalsIgnoreCase("Strafe")) {
            if ((mc.player.forwardSpeed != 0 || mc.player.sidewaysSpeed != 0)) {
                if (!mc.player.isSprinting()) {
                    mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
                }

                mc.player.setVelocity(new Vec3d(0, mc.player.getVelocity().y, 0));
                mc.player.updateVelocity((float) strafeSpeed.getValue(), new Vec3d(mc.player.sidewaysSpeed, 0, mc.player.forwardSpeed));

                double vel = Math.abs(mc.player.getVelocity().getX()) + Math.abs(mc.player.getVelocity().getZ());

                if (vel >= 0.12 && mc.player.isOnGround()) {
                    mc.player.updateVelocity(vel >= 0.3 ? 0.0f : 0.15f, new Vec3d(mc.player.sidewaysSpeed, 0, mc.player.forwardSpeed));
                    mc.player.jump();
                }
            }
        }
        else if (mode.getMode().equalsIgnoreCase("Bhop")) {
            if (mc.player.forwardSpeed > 0 && mc.player.isOnGround()) {
                double speeds = 0.65 + speed.getValue() / 30;
                mc.player.jump();
                mc.player.setVelocity(mc.player.getVelocity().x * speeds, 0.255556, mc.player.getVelocity().z * speeds);
                mc.player.sidewaysSpeed += 3.0F;
                mc.player.jump();
                mc.player.setSprinting(true);
            }
        }
    }
}
