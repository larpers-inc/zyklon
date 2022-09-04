package me.vp.zyklon.util;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.List;

public class WorldUtils {
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static List<WorldChunk> getLoadedChunks() {
        List<WorldChunk> chunks = new ArrayList<>();
        int viewDist = mc.options.getViewDistance().getValue();

        for (int x = -viewDist; x <= viewDist; x++) {
            for (int z = -viewDist; z <= viewDist; z++) {
                WorldChunk chunk = mc.world.getChunkManager().getWorldChunk((int) mc.player.getX() / 16 + x, (int) mc.player.getZ() / 16 + z);
                if (chunk != null) chunks.add(chunk);
            }
        }
        return chunks;
    }

    public static List<BlockEntity> getBlockEntities() {
        List<BlockEntity> list = new ArrayList<>();
        getLoadedChunks().forEach(c -> list.addAll(c.getBlockEntities().values()));
        return list;
    }


    public static Block checkBlock(Direction dir) {
        return mc.world.getBlockState(mc.player.getBlockPos().offset(dir, 1)).getBlock();
    }

    public static boolean isPlaceable(BlockPos pos, boolean entityCheck) {
        if (!mc.world.getBlockState(pos).getMaterial().isReplaceable()) return false;
        for (Entity e : mc.world.getEntitiesByClass(Entity.class, new Box(pos), e -> !(e instanceof ExperienceBottleEntity
                                                                                       || e instanceof ItemEntity
                                                                                       || e instanceof ExperienceOrbEntity))) {
            if (e instanceof PlayerEntity) return false;
            return !entityCheck;
        }
        return true;
    }

    public static Direction getBlockSide(BlockPos pos) {
        for (Direction d : Direction.values())
            if (!mc.world.getBlockState(pos.add(d.getVector())).getMaterial().isReplaceable()) return d;
        return null;
    }

    public static boolean doesBoxCollide(Box box) {
        for (int x = (int) Math.floor(box.minX); x < Math.ceil(box.maxX); x++) {
            for (int y = (int) Math.floor(box.minY); y < Math.ceil(box.maxY); y++) {
                for (int z = (int) Math.floor(box.minZ); z < Math.ceil(box.maxZ); z++) {
                    int fx = x, fy = y, fz = z;
                    if (mc.world.getBlockState(new BlockPos(x, y, z)).getCollisionShape(mc.world, new BlockPos(x, y, z)).getBoundingBoxes().stream()
                            .anyMatch(b -> b.offset(fx, fy, fz).intersects(box))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }


}
