package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.ModeSetting;
import dev.vili.zyklon.setting.settings.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Fly extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", this, "Vanilla", "Vanilla", "Static");
    public final ModeSetting antikick = new ModeSetting("AntiKick", this, "Fall", "Fall", "Packet", "OnGround", "None");
    public final NumberSetting speed = new NumberSetting("Speed", this, 2, 1, 10, 0.1);

    int antiKickTimer = 0;
    public Fly() {
        super("Fly", "Allows you to fly.", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
        this.addSettings(mode, antikick, speed);
    }

    @Override
    public void onDisable() {
        mc.player.getAbilities().allowFlying = false;
        mc.player.getAbilities().flying = false;
        mc.player.getAbilities().setFlySpeed(0.05f);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        float flySpeed = (int) speed.getValue();

        mc.player.setOnGround(mc.interactionManager.isBreakingBlock());

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

        else if (mode.is("Vanilla")) {
            mc.player.getAbilities().allowFlying = true;
            mc.player.getAbilities().flying = true;
            mc.player.getAbilities().setFlySpeed(flySpeed / 10f);
        }

        if (antikick.is("Fall")) {
            antiKickTimer++;
            if (antiKickTimer > 20 && mc.player.world.getBlockState(new BlockPos(mc.player.getPos().subtract(0, 0.0433D, 0))).isAir()) {
                antiKickTimer = 0;
                mc.player.setPos(mc.player.getX(), mc.player.getY() - 0.0433D, mc.player.getZ());
            }
        } else if (antikick.is("Packet")) {
            antiKickTimer++;
            if (antiKickTimer > 20) {
                antiKickTimer = 0;
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 0.0433D, mc.player.getZ(), false));
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() - 0.0433D, mc.player.getZ(), true));
            }
        } else if (antikick.is("OnGround")) {
            antiKickTimer++;
            if (antiKickTimer > 20) {
                antiKickTimer = 0;
                mc.player.setOnGround(true);
            }
        } else if (antikick.is("None")) {
            antiKickTimer = 0;
        }
    }
}