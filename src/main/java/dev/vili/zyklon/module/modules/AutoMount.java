package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import dev.vili.zyklon.setting.settings.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class AutoMount extends Module {
    public final BooleanSetting horses = new BooleanSetting("Horses", this, true);
    public final BooleanSetting donkeys = new BooleanSetting("Donkeys", this, true);
    public final BooleanSetting mules = new BooleanSetting("Mules", this, true);
    public final BooleanSetting llamas = new BooleanSetting("Llamas", this, true);
    public final BooleanSetting pigs = new BooleanSetting("Pigs", this, true);
    public final BooleanSetting striders = new BooleanSetting("Striders", this, true);
    public final BooleanSetting boats = new BooleanSetting("Boats", this, true);
    public final BooleanSetting minecarts = new BooleanSetting("Minecarts", this, true);
    public final BooleanSetting saddledOnly = new BooleanSetting("SaddledOnly", this, true);

    public AutoMount() {
        super("AutoMount", "Automatically mounts a rideable entities.", GLFW.GLFW_KEY_UNKNOWN, Category.MISC);
        this.addSettings(horses, donkeys, mules, llamas, pigs, striders, boats, minecarts, saddledOnly);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player.hasVehicle()) return;

        for (Entity entity : mc.world.getEntities()) {
            if (mc.player.getMainHandStack().getItem() instanceof SpawnEggItem) return;

            if (donkeys.isEnabled() && entity instanceof DonkeyEntity && (!saddledOnly.isEnabled() || ((DonkeyEntity) entity).isSaddled())) {
                mc.interactionManager.interactEntity(mc.player, entity, Hand.MAIN_HAND);
            } else if (llamas.isEnabled() && entity instanceof LlamaEntity) {
                mc.interactionManager.interactEntity(mc.player, entity, Hand.MAIN_HAND);
            } else if (boats.isEnabled() && entity instanceof BoatEntity) {
                mc.interactionManager.interactEntity(mc.player, entity, Hand.MAIN_HAND);
            } else if (minecarts.isEnabled() && entity instanceof MinecartEntity) {
                mc.interactionManager.interactEntity(mc.player, entity, Hand.MAIN_HAND);
            } else if (horses.isEnabled() && entity instanceof HorseEntity && (!saddledOnly.isEnabled() || ((HorseEntity) entity).isSaddled())) {
                mc.interactionManager.interactEntity(mc.player, entity, Hand.MAIN_HAND);
            } else if (pigs.isEnabled() && entity instanceof PigEntity && ((PigEntity) entity).isSaddled()) {
                mc.interactionManager.interactEntity(mc.player, entity, Hand.MAIN_HAND);
            } else if (mules.isEnabled() && entity instanceof MuleEntity && (!saddledOnly.isEnabled() || ((MuleEntity) entity).isSaddled())) {
                mc.interactionManager.interactEntity(mc.player, entity, Hand.MAIN_HAND);
            } else if (striders.isEnabled() && entity instanceof StriderEntity) {
                mc.interactionManager.interactEntity(mc.player, entity, Hand.MAIN_HAND);
            } else if (horses.isEnabled() && entity instanceof SkeletonHorseEntity && (!saddledOnly.isEnabled() || ((SkeletonHorseEntity) entity).isSaddled())) {
                mc.interactionManager.interactEntity(mc.player, entity, Hand.MAIN_HAND);
            }
        }
    }
}
