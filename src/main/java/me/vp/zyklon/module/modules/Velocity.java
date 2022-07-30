package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.PacketEvent;
import me.vp.zyklon.event.events.PlayerPushedEvent;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.NumberSetting;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import org.lwjgl.glfw.GLFW;
import org.quantumclient.energy.Subscribe;

public class Velocity extends Module {
    public final NumberSetting velocityX = new NumberSetting("VelocityX", this, 0, 0, 100, 1);
    public final NumberSetting velocityY = new NumberSetting("VelocityY", this, 0, 0, 100, 1);

    public Velocity() {
        super("Velocity", "Prevents you from taking knockback.", GLFW.GLFW_KEY_UNKNOWN, Category.PLAYER);
		this.addSettings(velocityX, velocityY);
	}

    @Subscribe
    public void playerPushed(PlayerPushedEvent event) {
        double amount = velocityX.getValue() / 100d;
        event.setPushX(event.getPushX() * amount);
        event.setPushY(event.getPushY() * amount);
        event.setPushZ(event.getPushZ() * amount);
    }

    @Subscribe
    public void onPacket(PacketEvent.Receive event) {
        if (mc.player == null)
			return;

		if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket) {
			EntityVelocityUpdateS2CPacket packet = (EntityVelocityUpdateS2CPacket) event.getPacket();
            if (packet.getId() == mc.player.getId()) {
				double velXZ = velocityX.getValue() / 100;
				double velY = velocityY.getValue() / 100;

				double pvelX = (packet.getVelocityX() / 8000d - mc.player.getVelocity().x) * velXZ;
				double pvelY = (packet.getVelocityY() / 8000d - mc.player.getVelocity().y) * velY;
				double pvelZ = (packet.getVelocityZ() / 8000d - mc.player.getVelocity().z) * velXZ;

				packet.velocityX = (int) (pvelX * 8000 + mc.player.getVelocity().x * 8000);
				packet.velocityY = (int) (pvelY * 8000 + mc.player.getVelocity().y * 8000);
				packet.velocityZ = (int) (pvelZ * 8000 + mc.player.getVelocity().z * 8000);
			}
		} else if (event.getPacket() instanceof ExplosionS2CPacket) {
			ExplosionS2CPacket packet = (ExplosionS2CPacket) event.getPacket();

			double velXZ = velocityX.getValue() / 100;
			double velY = velocityY.getValue() / 100;

			packet.playerVelocityX = (float) (packet.getPlayerVelocityX() * velXZ);
			packet.playerVelocityY = (float) (packet.getPlayerVelocityY() * velY);
			packet.playerVelocityZ = (float) (packet.getPlayerVelocityZ() * velXZ);
		}
    }
}