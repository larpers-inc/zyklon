package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.PacketEvent;
import me.vp.zyklon.event.events.TickEvent;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.ModeSetting;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import org.lwjgl.glfw.GLFW;
import me.vp.zyklon.eventbus.Subscribe;

public class Sneak extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", this, "Legit", "Legit", "Packet");
    public Sneak() {
        super("Sneak", "Automatically sneaks.", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
        settings.add(mode);
    }

    private boolean packetSent;

    @Override
    public void onEnable() {
        if (mode.is("Packet")) mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
    }

    @Override
    public void onDisable() {
        if (mode.is("Packet")) mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mode.is("Legit")) mc.options.sneakKey.setPressed(true);
        else if (mode.is("Packet") && !packetSent) {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
            packetSent = true;
        }
    }

    @Subscribe
    public void onPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof ClientCommandC2SPacket) {
            ClientCommandC2SPacket packet = (ClientCommandC2SPacket) event.getPacket();
            if (packet.getMode() == ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY)
                event.setCancelled(true);
        }
    }
}