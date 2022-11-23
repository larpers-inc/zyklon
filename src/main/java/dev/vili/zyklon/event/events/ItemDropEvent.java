package dev.vili.zyklon.event.events;

import dev.vili.zyklon.event.Event;
import net.minecraft.item.ItemStack;

public class ItemDropEvent extends Event {
    public ItemStack itemStack;

    public ItemDropEvent get(ItemStack itemStack) {
        this.setCancelled(false);
        this.itemStack = itemStack;
        return this;
    }
}
