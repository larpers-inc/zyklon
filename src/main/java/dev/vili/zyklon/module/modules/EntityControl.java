package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.EntityControlEvent;
import dev.vili.zyklon.event.events.PacketEvent;
import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import dev.vili.zyklon.setting.settings.NumberSetting;
import dev.vili.zyklon.util.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

/* Credits to @BleachDev, my beloved */
public class EntityControl extends Module {
    public final NumberSetting entitySpeed = new NumberSetting("EntitySpeed", this, 1.2, 0, 5, 0.1);
    public final BooleanSetting entityFly = new BooleanSetting("EntityFly", this, false);
    public final NumberSetting ascend = new NumberSetting("Ascend", this, 1, 0, 5, 0.1);
    public final NumberSetting descend = new NumberSetting("Descend", this, 1, 0, 5, 0.1);
    public final BooleanSetting snap = new BooleanSetting("Snap", this, true);
    public final BooleanSetting maxJump = new BooleanSetting("MaxJump", this, false);
    public final BooleanSetting antiStuck = new BooleanSetting("AntiStuck", this, true);
    public final BooleanSetting noAi = new BooleanSetting("NoAi", this, true);
    public final BooleanSetting antiDismount = new BooleanSetting("AntiDismount", this, false);

    public EntityControl() {
        super("EntityControl", "Allows you to ride entities.", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
        this.addSettings(entitySpeed, entityFly, ascend, descend, snap, maxJump, antiStuck, noAi, antiDismount);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player.getVehicle() == null)
            return;

        Entity e = mc.player.getVehicle();
        double speed = entitySpeed.getValue();

        double forward = mc.player.forwardSpeed;
        double strafe = mc.player.sidewaysSpeed;
        float yaw = mc.player.getYaw();

        e.setYaw(yaw);
        if (e instanceof LlamaEntity) {
            ((LlamaEntity) e).headYaw = mc.player.headYaw;
        }

        if (noAi.isEnabled() && forward == 0 && strafe == 0) {
            e.setVelocity(new Vec3d(0, e.getVelocity().y, 0));
        }


        if (forward != 0.0D) {
            if (strafe > 0.0D) {
                yaw += (forward > 0.0D ? -45 : 45);
            } else if (strafe < 0.0D) {
                yaw += (forward > 0.0D ? 45 : -45);
            }

            if (forward > 0.0D) {
                forward = 1.0D;
            } else if (forward < 0.0D) {
                forward = -1.0D;
            }

            strafe = 0.0D;
        }

        e.setVelocity(forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F)),
                e.getVelocity().y,
                forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F)));


        if (entityFly.isEnabled()) {
            if (mc.options.jumpKey.isPressed()) {
                e.setVelocity(e.getVelocity().x, ascend.getValue(), e.getVelocity().z);
            } else {
                e.setVelocity(e.getVelocity().x, -descend.getValue(), e.getVelocity().z);
            }
        }

        if (snap.isEnabled()) {
            BlockPos p = new BlockPos(e.getPos());
            if (!mc.world.getBlockState(p.down()).getMaterial().isReplaceable() && e.fallDistance > 0.01) {
                e.setVelocity(e.getVelocity().x, -1, e.getVelocity().z);
            }
        }

        if (antiStuck.isEnabled()) {
            Vec3d vel = e.getVelocity().multiply(2);
            if (WorldUtils.doesBoxCollide(e.getBoundingBox().offset(vel.x, 0, vel.z))) {
                for (int i = 2; i < 10; i++) {
                    if (!WorldUtils.doesBoxCollide(e.getBoundingBox().offset(vel.x / i, 0, vel.z / i))) {
                        e.setVelocity(vel.x / i / 2, vel.y, vel.z / i / 2);
                        break;
                    }
                }
            }
        }
    }

    @Subscribe
    public void onPacket(PacketEvent.Send event) {
        if (antiDismount.isEnabled() && event.getPacket() instanceof VehicleMoveC2SPacket && mc.player.hasVehicle()) {
            mc.interactionManager.interactEntity(mc.player, mc.player.getVehicle(), Hand.MAIN_HAND);
        }
    }

    @Subscribe
    public void onPacket(PacketEvent.Receive event) {
        if (antiDismount.isEnabled() && mc.player != null && mc.player.hasVehicle() && !mc.player.input.sneaking
                && (event.getPacket() instanceof PlayerPositionLookS2CPacket || event.getPacket() instanceof EntityPassengersSetS2CPacket)) {
            event.setCancelled(true);
        }
    }

    @Subscribe
    public void onEntityControl(EntityControlEvent event) {
        if (mc.player.getVehicle() instanceof ItemSteerable && mc.player.forwardSpeed == 0 && mc.player.sidewaysSpeed == 0) {
            return;
        }

        event.setControllable(true);
    }

}

