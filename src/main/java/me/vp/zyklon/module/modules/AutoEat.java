package me.vp.zyklon.module.modules;

import me.vp.zyklon.Zyklon;
import me.vp.zyklon.event.events.TickEvent;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.BooleanSetting;
import me.vp.zyklon.setting.settings.NumberSetting;
import me.vp.zyklon.util.InventoryUtils;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.FoodComponents;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import me.vp.zyklon.eventbus.Subscribe;

public class AutoEat extends Module {
    public final BooleanSetting hunger = new BooleanSetting("hunger", this, true);
    public final NumberSetting hungryLevel = new NumberSetting("Hungerlvl", this, 14, 0, 19, 1);
    public final BooleanSetting health = new BooleanSetting("health", this, true);
    public final NumberSetting healthLevel = new NumberSetting("Healthlvl", this, 14, 0, 19, 1);
    public final BooleanSetting gapples = new BooleanSetting("Gapples", this, true);
    public final BooleanSetting preferGapples = new BooleanSetting("PreferGapples", this, false);

    public AutoEat() {
        super("AutoEat", "Automatically eats food.", GLFW.GLFW_KEY_UNKNOWN, Category.PLAYER);
        this.addSettings(hunger, hungryLevel, health, healthLevel, gapples, preferGapples);
    }

    private boolean eating;


    @Override
    public void onDisable() {
        mc.options.useKey.setPressed(false);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (eating && mc.options.useKey.isPressed() && !mc.player.isUsingItem()) {
            eating = false;
            mc.options.useKey.setPressed(false);
        }

        if (hunger.isEnabled() && mc.player.getHungerManager().getFoodLevel() <= hungryLevel.getValue())
            startEating();
        else if (health.isEnabled() && (int) mc.player.getHealth() + (int) mc.player.getAbsorptionAmount() <= healthLevel.getValue())
            startEating();
    }

    public void startEating() {
        int slot = -1;
        int hunger = -1;

        for (int s: InventoryUtils.getInventorySlots(true)) {
            FoodComponent food = mc.player.getInventory().getStack(s).getItem().getFoodComponent();

            if (food == null)
                continue;

            int h = preferGapples.isEnabled() && (food == FoodComponents.GOLDEN_APPLE || food == FoodComponents.ENCHANTED_GOLDEN_APPLE)
                    ? Integer.MAX_VALUE : food.getHunger();

            if (h <= hunger || (!gapples.isEnabled() && (food == FoodComponents.GOLDEN_APPLE || food == FoodComponents.ENCHANTED_GOLDEN_APPLE)))
                continue;

            slot = s;
            hunger = h;
        }

        if (hunger != -1) {
            if (slot == mc.player.getInventory().selectedSlot || slot == 40) {
                mc.options.useKey.setPressed(true);
                mc.interactionManager.interactItem(mc.player, slot == 40 ? Hand.OFF_HAND : Hand.MAIN_HAND);
                eating = true;
            } else {
                InventoryUtils.selectSlot(slot);
            }
        }
    }
}

