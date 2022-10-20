package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.PacketEvent;
import me.vp.zyklon.eventbus.Subscribe;
import me.vp.zyklon.module.Module;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import org.lwjgl.glfw.GLFW;

public class NoWeather extends Module {

    public NoWeather() {
        super("NoWeather", "Disables weather", GLFW.GLFW_KEY_UNKNOWN, Module.Category.RENDER);
    }

    @Subscribe
    public void onPacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof PlaySoundS2CPacket packet) {
            if (packet.getSound().equals(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER)) {
                event.setCancelled(true);
            } else if (packet.getSound().equals(SoundEvents.WEATHER_RAIN) || packet.getSound().equals(SoundEvents.WEATHER_RAIN_ABOVE)) {
                event.setCancelled(true);
            }
        }
    }
}
