package me.vp.zyklon.event;

import net.minecraft.client.MinecraftClient;

public class Event extends me.vp.zyklon.eventbus.Event {
    public MinecraftClient mc = MinecraftClient.getInstance();
    public Event() {}
}
