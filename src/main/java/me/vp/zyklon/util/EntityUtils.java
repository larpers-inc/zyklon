package me.vp.zyklon.util;

import me.vp.zyklon.Zyklon;

import me.vp.zyklon.setting.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.world.GameMode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EntityUtils {
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final ArrayList<PlayerEntity> list = new ArrayList<>();

    public static int getEntityPing(PlayerEntity entity) {
        if (mc.getNetworkHandler() == null) return 0;
        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(entity.getUuid());
        if (playerListEntry == null) return 0;
        return playerListEntry.getLatency();
    }

    public static GameMode getEntityGamemode(PlayerEntity entity) {
        if (entity == null) return null;
        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(entity.getUuid());
        return playerListEntry == null ? null : playerListEntry.getGameMode();
    }

    public static UUID getOwnerUUID(LivingEntity livingEntity) {
        if (livingEntity instanceof TameableEntity tameableEntity) {
            if (tameableEntity.isTamed()) {
                return tameableEntity.getOwnerUuid();
            }
        }
        if (livingEntity instanceof HorseEntity horseBaseEntity) {
            return horseBaseEntity.getOwnerUuid();
        }
        return null;
    }

    public static float getFullHealth(LivingEntity entity) {
        return entity.getHealth() + entity.getAbsorptionAmount();
    }

    public static void selectEntities(List<Entity> list, NumberSetting range) {
        list.clear();
        for (Entity entity : mc.world.getEntities())
            if (mc.player.distanceTo(entity) <= range.getValue()) list.add(entity);
    }

    public static ArrayList<PlayerEntity> selectPlayers(NumberSetting range) {
        list.clear();
        for (Entity entity : mc.world.getEntities()) {
            if (mc.player.distanceTo(entity) <= range.getValue() && entity instanceof PlayerEntity playerEntity)
                list.add(playerEntity);
        }
        return list;
    }

    public static boolean isAnimal(Entity e) {
        return e instanceof PassiveEntity
                || e instanceof AmbientEntity
                || e instanceof WaterCreatureEntity
                || e instanceof IronGolemEntity
                || e instanceof SnowGolemEntity;
    }

    public static boolean isMob(Entity e) {
        return e instanceof Monster;
    }

    public static boolean isPlayer(Entity e) {
        return e instanceof PlayerEntity;
    }

    public static boolean isOtherServerPlayer(Entity e) {
        return e instanceof PlayerEntity
                && e != MinecraftClient.getInstance().player;
    }

    public static boolean isFriend(Entity e) {
        return e instanceof PlayerEntity && Zyklon.INSTANCE.friendManager.getFriends().contains(e.getName().getString());
    }

    public static boolean isAttackable(Entity e, boolean ignoreFriends) {
        return (e instanceof LivingEntity || e instanceof ShulkerBulletEntity || e instanceof AbstractFireballEntity)
                && e.isAlive()
                && e != MinecraftClient.getInstance().player
                && !e.isConnectedThroughVehicle(MinecraftClient.getInstance().player);
    }
}