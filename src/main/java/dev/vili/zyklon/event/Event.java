package dev.vili.zyklon.event;

import net.minecraft.client.MinecraftClient;

public class Event extends dev.vili.zyklon.eventbus.Event {
    public MinecraftClient mc = MinecraftClient.getInstance();
    public Event() {}
}
