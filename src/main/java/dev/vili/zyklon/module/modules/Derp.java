package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.lwjgl.glfw.GLFW;

public class Derp extends Module {
    public final BooleanSetting hitRandomly = new BooleanSetting("HitRandomly", this, false);
    public final BooleanSetting clientSide = new BooleanSetting("ClientSide", this, false);

    int i;
    public Derp() {
        super("Derp", "Do random shit.", GLFW.GLFW_KEY_UNKNOWN, Category.MISC);
        this.addSettings(hitRandomly, clientSide);
    }

    @Subscribe
    public void onTick() {
        if (mc.world == null || mc.player == null) return;

        if (hitRandomly.isEnabled()) {
            if (mc.player.age % 20 == 0) {
                mc.player.swingHand(mc.player.getActiveHand());
            }
        }

        int yaw = (int) (mc.player.getYaw() + (Math.random() * 360 - 180));
        int pitch = (int) (mc.player.getPitch() + (Math.random() * 360 - 180));

        if (clientSide.isEnabled()) {
            mc.player.setYaw(yaw);
            mc.player.setPitch(pitch);
        } else {
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, mc.player.isOnGround()));
        }
    }
}
