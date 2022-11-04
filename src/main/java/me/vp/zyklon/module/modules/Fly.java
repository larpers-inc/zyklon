package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.TickEvent;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.ModeSetting;
import me.vp.zyklon.setting.settings.NumberSetting;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import me.vp.zyklon.eventbus.Subscribe;

public class Fly extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", this, "Static", "Static", "JetPack", "Vanilla");
    public final ModeSetting bypass = new ModeSetting("Bypass", this, "Off", "Off", "Packet", "Fall");
    public final NumberSetting speed = new NumberSetting("Speed", this, 2, 1, 10, 0.1);
    public Fly() {
        super("Fly", "Allows you to fly.", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
        this.addSettings(mode, bypass, speed);
    }

    @Override
    public void onDisable() {
        mc.player.getAbilities().flying = false;
        mc.player.getAbilities().setFlySpeed(0.05f);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        float flySpeed = (int) speed.getValue();

        if (mode.is("Static")) {
            if (mc.player.isRiding()) {
                Entity riding = mc.player.getRootVehicle();
                Vec3d velocity = riding.getVelocity();
                double motionY = mc.options.jumpKey.isPressed() ? 0.3 : 0;
                riding.setVelocity(velocity.x, motionY, velocity.z);
            } else {
                if (mc.options.sprintKey.isPressed()) flySpeed *= 1.5;
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

        else if (mode.is("JetPack")) {
            if (InputUtil.isKeyPressed(mc.getWindow().getHandle(), InputUtil.fromTranslationKey(mc.options.jumpKey.getBoundKeyTranslationKey()).getCode())) {
				mc.player.jump();
			} else {
				if (InputUtil.isKeyPressed(mc.getWindow().getHandle(), InputUtil.fromTranslationKey(mc.options.jumpKey.getBoundKeyTranslationKey()).getCode())) {
					mc.player.updatePosition(mc.player.getX(), mc.player.getY() - flySpeed / 10f, mc.player.getZ());
				}
			}
        }

        else if (mode.is("Vanilla")) {
            mc.player.getAbilities().flying = true;
            mc.player.getAbilities().setFlySpeed(flySpeed / 10f);
        }

        if (bypass.is("Packet")) {
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() - 0.069, mc.player.getZ(), true));
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 0.069, mc.player.getZ(), true));
        }
        else if (bypass.is("Fall")) mc.world.getBlockState(new BlockPos(new BlockPos(mc.player.getPos().add(0, -0.069, 0)))).getMaterial();
    }
}