package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.TickEvent;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.NumberSetting;
import me.vp.zyklon.util.InventoryUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import me.vp.zyklon.eventbus.Subscribe;

import java.util.Comparator;

public class AutoFish extends Module {
    public AutoFish() {
        super("AutoFish", "Automatically catches fish.", GLFW.GLFW_KEY_UNKNOWN, Category.PLAYER);
    }

    private boolean threwRod;
    private boolean reeledFish;


    @Override
    public void onDisable() {
        threwRod = false;
        reeledFish = false;
    }

    @Subscribe
    public void onTick(TickEvent event) {
        Hand hand = getHandWithRod();
        if (mc.player.fishHook != null) {
            threwRod = false;

            boolean caughtFish = mc.player.fishHook.getDataTracker().get(FishingBobberEntity.CAUGHT_FISH);
            if (!reeledFish && caughtFish) {
                if (hand != null) {
                    // reel
                    mc.interactionManager.interactItem(mc.player, hand);
                    reeledFish = true;
                    return;
                }
            } else if (!caughtFish) {
                reeledFish = false;
            }
        }

        if (!threwRod && mc.player.fishHook == null) {
            if (hand != null) {
                // throw
                mc.interactionManager.interactItem(mc.player, hand);
                threwRod = true;
                reeledFish = false;
            }
        }
    }

    private Hand getHandWithRod() {
        return mc.player.getMainHandStack().getItem() == Items.FISHING_ROD ? Hand.MAIN_HAND
                : mc.player.getOffHandStack().getItem() == Items.FISHING_ROD ? Hand.OFF_HAND
                : null;
    }

}
