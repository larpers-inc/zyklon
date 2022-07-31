package me.vp.zyklon.module.modules;

import me.vp.zyklon.Zyklon;
import me.vp.zyklon.event.events.TickEvent;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.BooleanSetting;
import me.vp.zyklon.util.ZLogger;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.quantumclient.energy.Subscribe;

import java.awt.*;
import java.io.File;

public class XRay extends Module {

    public XRay() {
        super("XRay", "Allows you to see through blocks.", GLFW.GLFW_KEY_UNKNOWN, Category.RENDER);
    }

    @Override
    public void onDisable() {
        mc.worldRenderer.reload();
        Zyklon.INSTANCE.xrayManager.save();
    }

    @Override
    public void onEnable() {
        mc.worldRenderer.reload();
        Zyklon.INSTANCE.xrayManager.save();
    }

}

