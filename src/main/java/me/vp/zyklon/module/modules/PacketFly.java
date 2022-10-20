package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.ClientMoveEvent;
import me.vp.zyklon.event.events.PacketEvent;
import me.vp.zyklon.event.events.SendMovementPacketsEvent;
import me.vp.zyklon.event.events.TickEvent;
import me.vp.zyklon.eventbus.Subscribe;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.BooleanSetting;
import me.vp.zyklon.setting.settings.ModeSetting;
import me.vp.zyklon.setting.settings.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

/* Credits to @BleachDev */
public class PacketFly extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", this, "Phase", "Phase", "Packet");
    public final NumberSetting vSpeed = new NumberSetting("VerticalSpeed", this, 1, 0.1, 2, 0.1);
    public final NumberSetting hSpeed = new NumberSetting("HorizontalSpeed", this, 1, 0.1, 2, 0.1);
    public final NumberSetting fallSpeed = new NumberSetting("FallSpeed", this, 20, 1, 40, 1);
    public final BooleanSetting packetCancel = new BooleanSetting("PacketCancel", this, true);

    private Vec3d cachedPos;
    private int timer = 0;
    public PacketFly() {
        super("PacketFly", "Allows you to fly using packets.", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
        this.addSettings(mode, vSpeed, hSpeed, fallSpeed, packetCancel);
    }

    @Override
    public void onEnable() {
        cachedPos = mc.player.getRootVehicle().getPos();
    }

    @Subscribe
    public void onMovementPackets(SendMovementPacketsEvent event) {
        mc.player.setVelocity(Vec3d.ZERO);
        event.setCancelled(true);
    }

    @Subscribe
    public void onClientMove(ClientMoveEvent event) {
        event.setCancelled(true);
    }

    @Subscribe
    public void onPacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            PlayerPositionLookS2CPacket p = (PlayerPositionLookS2CPacket) event.getPacket();

            p.yaw = mc.player.getYaw();
            p.pitch = mc.player.getPitch();

            if (packetCancel.isEnabled()) {
                event.setCancelled(true);
            }
        }

    }

    @Subscribe
    public void onPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof PlayerMoveC2SPacket.LookAndOnGround) {
            event.setCancelled(true);
            return;
        }

        if (event.getPacket() instanceof PlayerMoveC2SPacket.Full) {
            event.setCancelled(true);
            PlayerMoveC2SPacket p = (PlayerMoveC2SPacket) event.getPacket();
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(p.getX(0), p.getY(0), p.getZ(0), p.isOnGround()));
        }
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (!mc.player.isAlive())
            return;

        double hspeed = hSpeed.getValue();
        double vspeed = vSpeed.getValue();
        timer++;

        Vec3d forward = new Vec3d(0, 0, hspeed).rotateY(-(float) Math.toRadians(mc.player.getYaw()));
        Vec3d moveVec = Vec3d.ZERO;

        if (mc.player.input.pressingForward) {
            moveVec = moveVec.add(forward);
        }
        if (mc.player.input.pressingBack) {
            moveVec = moveVec.add(forward.negate());
        }
        if (mc.player.input.jumping) {
            moveVec = moveVec.add(0, vspeed, 0);
        }
        if (mc.player.input.sneaking) {
            moveVec = moveVec.add(0, -vspeed, 0);
        }
        if (mc.player.input.pressingLeft) {
            moveVec = moveVec.add(forward.rotateY((float) Math.toRadians(90)));
        }
        if (mc.player.input.pressingRight) {
            moveVec = moveVec.add(forward.rotateY((float) -Math.toRadians(90)));
        }

        Entity target = mc.player.getRootVehicle();
        if (mode.is("Phase")) {
            if (timer > fallSpeed.getValue()) {
                moveVec = moveVec.add(0, -vspeed, 0);
                timer = 0;
            }

            cachedPos = cachedPos.add(moveVec);

            //target.noClip = true;
            target.updatePositionAndAngles(cachedPos.x, cachedPos.y, cachedPos.z, mc.player.getYaw(), mc.player.getPitch());
            if (target != mc.player) {
                mc.player.networkHandler.sendPacket(new VehicleMoveC2SPacket(target));
            } else {
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(cachedPos.x, cachedPos.y, cachedPos.z, false));
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(cachedPos.x, cachedPos.y - 0.01, cachedPos.z, true));
            }
        } else if (mode.is("Packet")) {
            //moveVec = Vec3d.ZERO;
			/*if (mc.player.headYaw != mc.player.yaw) {
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(
						mc.player.headYaw, mc.player.pitch, mc.player.isOnGround()));
				return;
			}*/

			/*if (mc.options.jumpKey.isPressed())
				mouseY = 0.062;
			if (mc.options.sneakKey.isPressed())
				mouseY = -0.062;*/

            if (timer > fallSpeed.getValue()) {
                moveVec = new Vec3d(0, -vspeed, 0);
                timer = 0;
            }

            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                    mc.player.getX() + moveVec.x, mc.player.getY() + moveVec.y, mc.player.getZ() + moveVec.z, false));

            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                    mc.player.getX() + moveVec.x, mc.player.getY() - 420.69, mc.player.getZ() + moveVec.z, true));
        }
    }

}
