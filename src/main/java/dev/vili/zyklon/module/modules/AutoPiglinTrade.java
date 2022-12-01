package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import dev.vili.zyklon.util.InventoryUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;

import org.lwjgl.glfw.GLFW;

public class AutoPiglinTrade extends Module {
    public final BooleanSetting autoEquip = new BooleanSetting("AutoEquip", this, true);
    public AutoPiglinTrade() {
        super("AutoPiglinTrade", "Automatically trades with piglins.", GLFW.GLFW_KEY_UNKNOWN, Category.MISC);
        this.addSettings(autoEquip);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PiglinEntity piglin) {
                if (autoEquip.isEnabled()) {
                    for (int s: InventoryUtils.getInventorySlots(true)) {
                        if (mc.player.getInventory().getStack(s).getItem() == Items.GOLD_INGOT) {
                            InventoryUtils.selectSlot(s);
                        }
                    }
                }
                if (((PiglinEntity) entity).getMainHandStack() == Items.GOLD_INGOT.getDefaultStack()) return;
                else mc.getNetworkHandler().sendPacket(PlayerInteractEntityC2SPacket.interact(piglin, mc.player.isSneaking(), getHandWithGold()));
            }
        }
    }

    private Hand getHandWithGold() {
        return mc.player.getMainHandStack().getItem() == Items.GOLD_INGOT ? Hand.MAIN_HAND
                : mc.player.getOffHandStack().getItem() == Items.GOLD_INGOT ? Hand.OFF_HAND
                : null;
    }
}
