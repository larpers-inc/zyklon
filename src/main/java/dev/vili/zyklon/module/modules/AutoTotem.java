package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.NumberSetting;
import dev.vili.zyklon.util.InventoryUtils;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.lwjgl.glfw.GLFW;

public class AutoTotem extends Module {
    public final NumberSetting health = new NumberSetting("Health", this, 16, 1, 20, 1);
    public final NumberSetting delay = new NumberSetting("Delay", this, 0, 0, 10, 1);

    public AutoTotem() {
        super("AutoTotem", "Automatically equips totems in your offhand.", GLFW.GLFW_KEY_UNKNOWN, Category.COMBAT);
        this.addSettings(health, delay);
    }

    int ticks = 0;

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player.getHealth() + mc.player.getAbsorptionAmount() <= health.getValue()) {
            int totemSlot = InventoryUtils.getSlot(true, i -> mc.player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING);
            boolean inHand = mc.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING;

            if (inHand) return;

            if (totemSlot != -1) {
                if (ticks >= delay.getValue()) {
                    ticks = 0;
                    for (int i = 9; i < 45; i++) {
                        if (mc.player.getInventory().getStack(i >= 36 ? i - 36 : i).getItem() == Items.TOTEM_OF_UNDYING) {
                            boolean itemInOffhand = !mc.player.getOffHandStack().isEmpty();
                            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player);
                            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 45, 0, SlotActionType.PICKUP, mc.player);

                            if (itemInOffhand) mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player);
                        }
                    }
                } else ticks++;
            }
        }
    }
}
