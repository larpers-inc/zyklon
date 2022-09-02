package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.TickEvent;
import me.vp.zyklon.event.events.WorldRenderEvent;
import me.vp.zyklon.eventbus.Subscribe;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.util.RenderUtils;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BreadCrumbs extends Module {
    private final float timer = 10;
    private float currentTick = 0;
    private final List<Vec3d> positions = new ArrayList<>();
    public BreadCrumbs() {
        super("BreadCrumbs", "Draws a line where you walk.", GLFW.GLFW_KEY_UNKNOWN, Category.RENDER);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        currentTick++;
        if (timer == currentTick) {
            currentTick = 0;
            positions.add(mc.player.getPos());
        }
    }

    @Subscribe
    public void onRender(WorldRenderEvent.Post event) {
         for (int i = 0; i < positions.size() - 1; i++) {
            RenderUtils.draw3DLine(event.getMatrix(), positions.get(i), positions.get(i + 1), new Color(0, 255, 0));
        }
    }
}