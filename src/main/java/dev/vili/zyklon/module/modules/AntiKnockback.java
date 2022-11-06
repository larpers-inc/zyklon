package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.PacketEvent;
import dev.vili.zyklon.event.events.PlayerPushedEvent;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.NumberSetting;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import org.lwjgl.glfw.GLFW;
import dev.vili.zyklon.eventbus.Subscribe;

public class AntiKnockback extends Module {
    public final NumberSetting x = new NumberSetting("X", this, 0, 0, 100, 1);
    public final NumberSetting y = new NumberSetting("Y", this, 0, 0, 100, 1);

    public AntiKnockback() {
        super("AntiKnockback", "Prevents you from taking knockback.", GLFW.GLFW_KEY_UNKNOWN, Category.PLAYER);
		this.addSettings(x, y);
	}

    @Subscribe
    public void playerPushed(PlayerPushedEvent event) {
        double amount = x.getValue() / 100d;
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
				double velXZ = x.getValue() / 100;
				double velY = y.getValue() / 100;

				double pvelX = (packet.getVelocityX() / 8000d - mc.player.getVelocity().x) * velXZ;
				double pvelY = (packet.getVelocityY() / 8000d - mc.player.getVelocity().y) * velY;
				double pvelZ = (packet.getVelocityZ() / 8000d - mc.player.getVelocity().z) * velXZ;

				packet.velocityX = (int) (pvelX * 8000 + mc.player.getVelocity().x * 8000);
				packet.velocityY = (int) (pvelY * 8000 + mc.player.getVelocity().y * 8000);
				packet.velocityZ = (int) (pvelZ * 8000 + mc.player.getVelocity().z * 8000);
			}
		} else if (event.getPacket() instanceof ExplosionS2CPacket) {
			ExplosionS2CPacket packet = (ExplosionS2CPacket) event.getPacket();

			double velX = x.getValue() / 100;
			double velY = y.getValue() / 100;

			packet.playerVelocityX = (float) (packet.getPlayerVelocityX() * velX);
			packet.playerVelocityY = (float) (packet.getPlayerVelocityY() * velY);
			packet.playerVelocityZ = (float) (packet.getPlayerVelocityZ() * velX);
		}
    }
}