package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.PacketEvent;
import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.mixin.accessor.PlayerMoveC2SPacketAccessor;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.lwjgl.glfw.GLFW;

public class AntiHunger extends Module {
    public final BooleanSetting water = new BooleanSetting("DisableOnWater", this, true);

    public AntiHunger() {
        super("AntiHunger", "Reduces hunger consumption.", GLFW.GLFW_KEY_UNKNOWN, Category.PLAYER);
    }

    private boolean lastOnGround;
    private boolean sendOnGroundTruePacket;
    private boolean ignorePacket;

    @Override
    public void onEnable() {
        if (mc.player != null) {
            lastOnGround = mc.player.isOnGround();
        }
        sendOnGroundTruePacket = true;
    }

    @Subscribe
    public void onPacket(PacketEvent.Send event) {
        if (ignorePacket) return;

        if (event.getPacket() instanceof ClientCommandC2SPacket) {
            ClientCommandC2SPacket.Mode mode = ((ClientCommandC2SPacket) event.getPacket()).getMode();

            if (mode == ClientCommandC2SPacket.Mode.START_SPRINTING || mode == ClientCommandC2SPacket.Mode.STOP_SPRINTING) {
                event.cancel();
            }
        }

        if (event.getPacket() instanceof PlayerMoveC2SPacket && mc.player.isOnGround() && mc.player.fallDistance <= 0.0 && !mc.interactionManager.isBreakingBlock()) {
            ((PlayerMoveC2SPacketAccessor) event.getPacket()).setOnGround(false);
        }
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (water.isEnabled() && mc.player.isTouchingWater()) {
            ignorePacket = true;
            return;
        }

        if (mc.player.isOnGround() && !lastOnGround && !sendOnGroundTruePacket) sendOnGroundTruePacket = true;

        if (mc.player.isOnGround() && sendOnGroundTruePacket) {
            ignorePacket = true;
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
            ignorePacket = false;

            sendOnGroundTruePacket = false;
        }

        lastOnGround = mc.player.isOnGround();
    }
}
