package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import net.minecraft.client.render.entity.PlayerModelPart;
import org.lwjgl.glfw.GLFW;

import java.util.Random;

public class SkinBlinker extends Module {
    private final Random random = new Random();

    public SkinBlinker() {
        super("SkinBlinker", "Blinks your skins bodyparts.", GLFW.GLFW_KEY_UNKNOWN, Category.MISC);
    }

    @Override
    public void onDisable() {
        for (PlayerModelPart part : PlayerModelPart.values())
            mc.options.togglePlayerModelPart(part, true);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (random.nextInt(4) != 0) return;

        for (PlayerModelPart part : PlayerModelPart.values())
            mc.options.togglePlayerModelPart(part, !mc.options.isPlayerModelPartEnabled(part));
    }
}
