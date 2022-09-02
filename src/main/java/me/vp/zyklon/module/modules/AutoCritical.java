package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.PacketEvent;
import me.vp.zyklon.eventbus.Subscribe;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.ModeSetting;
import me.vp.zyklon.util.PlayerUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.lwjgl.glfw.GLFW;

public class AutoCritical extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", this, "Strict", "Strict", "Jump");
    public AutoCritical() {
        super("AutoCritical", "Automatically does critical hits.", GLFW.GLFW_KEY_UNKNOWN, Category.COMBAT);
        this.addSettings(mode);
    }

    @Subscribe
    public void onPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof PlayerInteractEntityC2SPacket) {
			PlayerInteractEntityC2SPacket packet = (PlayerInteractEntityC2SPacket) event.getPacket();
			if (PlayerUtils.getInteractType(packet) == PlayerUtils.InteractType.ATTACK
					&& PlayerUtils.getEntity(packet) instanceof LivingEntity) {
				sendCritPackets();
			}
		}
    }

    private void sendCritPackets() {
		if (mc.player.isClimbing() || mc.player.isTouchingWater()
			|| mc.player.hasStatusEffect(StatusEffects.BLINDNESS) || mc.player.hasVehicle()) {
			return;
		}

		boolean sprinting = mc.player.isSprinting();
		if (sprinting) {
			mc.player.setSprinting(false);
			mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
		}

		if (mc.player.isOnGround()) {
			double x = mc.player.getX();
			double y = mc.player.getY();
			double z = mc.player.getZ();
			if (mode.is("Strict")) {
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.0633, z, false));
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false));
			} else {
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.42, z, false));
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.65, z, false));
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.72, z, false));
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.53, z, false));
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.32, z, false));
			}
		}

		if (sprinting) {
			mc.player.setSprinting(true);
			mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
		}
	}

}