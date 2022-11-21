package dev.vili.zyklon.util;

import net.minecraft.item.*;

public class ItemUtils {

    public static boolean isThrowable(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof BowItem || item instanceof SnowballItem || item instanceof EggItem
                || item instanceof EnderPearlItem || item instanceof SplashPotionItem
                || item instanceof LingeringPotionItem || item instanceof FishingRodItem;
    }
    public static boolean isPlantable(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.WHEAT_SEEDS || item == Items.CARROT || item == Items.POTATO;
    }
}
