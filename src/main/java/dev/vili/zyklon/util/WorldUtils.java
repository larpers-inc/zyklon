package dev.vili.zyklon.util;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorldUtils {
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final Set<Block> RIGHTCLICKABLE_BLOCKS = Sets.newHashSet(
            Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST,
            Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX,
            Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX,
            Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX,
            Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX,
            Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX,
            Blocks.BLACK_SHULKER_BOX, Blocks.ANVIL, Blocks.BELL,
            Blocks.OAK_BUTTON, Blocks.ACACIA_BUTTON, Blocks.BIRCH_BUTTON, Blocks.DARK_OAK_BUTTON,
            Blocks.JUNGLE_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.STONE_BUTTON, Blocks.COMPARATOR,
            Blocks.REPEATER, Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE,
            Blocks.JUNGLE_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.ACACIA_FENCE_GATE,
            Blocks.BREWING_STAND, Blocks.DISPENSER, Blocks.DROPPER,
            Blocks.LEVER, Blocks.NOTE_BLOCK, Blocks.JUKEBOX,
            Blocks.BEACON, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED,
            Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED,
            Blocks.ORANGE_BED, Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.RED_BED, Blocks.WHITE_BED,
            Blocks.YELLOW_BED, Blocks.FURNACE, Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR,
            Blocks.BIRCH_DOOR, Blocks.JUNGLE_DOOR, Blocks.ACACIA_DOOR,
            Blocks.DARK_OAK_DOOR, Blocks.CAKE, Blocks.ENCHANTING_TABLE,
            Blocks.DRAGON_EGG, Blocks.HOPPER, Blocks.REPEATING_COMMAND_BLOCK,
            Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.CRAFTING_TABLE,
            Blocks.ACACIA_TRAPDOOR, Blocks.BIRCH_TRAPDOOR, Blocks.DARK_OAK_TRAPDOOR,
            Blocks.JUNGLE_TRAPDOOR, Blocks.OAK_TRAPDOOR, Blocks.SPRUCE_TRAPDOOR,
            Blocks.CAKE, Blocks.ACACIA_SIGN, Blocks.ACACIA_WALL_SIGN,
            Blocks.BIRCH_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.DARK_OAK_SIGN,
            Blocks.DARK_OAK_WALL_SIGN, Blocks.JUNGLE_SIGN, Blocks.JUNGLE_WALL_SIGN,
            Blocks.OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_SIGN,
            Blocks.SPRUCE_WALL_SIGN, Blocks.CRIMSON_SIGN, Blocks.CRIMSON_WALL_SIGN,
            Blocks.WARPED_SIGN, Blocks.WARPED_WALL_SIGN, Blocks.BLAST_FURNACE, Blocks.SMOKER,
            Blocks.CARTOGRAPHY_TABLE, Blocks.GRINDSTONE, Blocks.LECTERN, Blocks.LOOM,
            Blocks.STONECUTTER, Blocks.SMITHING_TABLE);

    public static final Set<Material> FLUIDS = Sets.newHashSet(Material.WATER, Material.LAVA, Material.UNDERWATER_PLANT, Material.REPLACEABLE_UNDERWATER_PLANT);
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

    public static boolean placeBlock(BlockPos pos, int slot, int rotateMode, boolean forceLegit, boolean airPlace, boolean swingHand) {
        if (!mc.world.isInBuildLimit(pos) || !isBlockEmpty(pos))
            return false;

        for (Direction d : Direction.values()) {
            if (!mc.world.isInBuildLimit(pos.offset(d)))
                continue;

            Block neighborBlock = mc.world.getBlockState(pos.offset(d)).getBlock();

            if (!airPlace && neighborBlock.getDefaultState().getMaterial().isReplaceable())
                continue;

            Vec3d vec = getLegitLookPos(pos.offset(d), d.getOpposite(), true, 5);

            if (vec == null) {
                if (forceLegit) {
                    continue;
                }

                vec = getLegitLookPos(pos.offset(d), d.getOpposite(), false, 5);

                if (vec == null) {
                    continue;
                }
            }

            int prevSlot = mc.player.getInventory().selectedSlot;
            Hand hand = InventoryUtils.selectSlot(slot);

            if (hand == null) {
                return false;
            }

            if (rotateMode == 1) {
                facePosPacket(vec.x, vec.y, vec.z);
            } else if (rotateMode == 2) {
                facePos(vec.x, vec.y, vec.z);
            }

            if (RIGHTCLICKABLE_BLOCKS.contains(neighborBlock)) {
                mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
            }

            if (swingHand) {
                mc.player.swingHand(hand);
            } else {
                mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(hand));
            }

            mc.interactionManager.interactBlock(mc.player, hand, new BlockHitResult(Vec3d.of(pos), airPlace ? d : d.getOpposite(), airPlace ? pos : pos.offset(d), false));

            if (RIGHTCLICKABLE_BLOCKS.contains(neighborBlock))
                mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));

            mc.player.getInventory().selectedSlot = prevSlot;

            return true;
        }

        return false;
    }

    public static boolean placeBlock(final BlockPos pos, final Direction d) {
        if (!mc.world.isInBuildLimit(pos) || !isBlockEmpty(pos)) return false;

        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(pos).add(Vec3d.of(d.getVector()).multiply(0.5)),
                d.getOpposite(), pos.offset(d), false));
        return true;
    }

    public static Vec3d getLegitLookPos(BlockPos pos, Direction dir, boolean raycast, int res) {
        return getLegitLookPos(new Box(pos), dir, raycast, res, 0.01);
    }

    public static Vec3d getLegitLookPos(Box box, Direction dir, boolean raycast, int res, double extrude) {
        Vec3d eyePos = mc.player.getEyePos();
        Vec3d blockPos = new Vec3d(box.minX, box.minY, box.minZ).add(
                (dir == Direction.WEST ? -extrude : dir.getOffsetX() * box.getXLength() + extrude),
                (dir == Direction.DOWN ? -extrude : dir.getOffsetY() * box.getYLength() + extrude),
                (dir == Direction.NORTH ? -extrude : dir.getOffsetZ() * box.getZLength() + extrude));

        for (double i = 0; i <= 1; i += 1d / (double) res) {
            for (double j = 0; j <= 1; j += 1d / (double) res) {
                Vec3d lookPos = blockPos.add(
                        (dir.getAxis() == Direction.Axis.X ? 0 : i * box.getXLength()),
                        (dir.getAxis() == Direction.Axis.Y ? 0 : dir.getAxis() == Direction.Axis.Z ? j * box.getYLength() : i * box.getYLength()),
                        (dir.getAxis() == Direction.Axis.Z ? 0 : j * box.getZLength()));

                if (eyePos.distanceTo(lookPos) > 4.55)
                    continue;

                if (raycast) {
                    if (mc.world.raycast(new RaycastContext(eyePos, lookPos,
                            RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player)).getType() == HitResult.Type.MISS) {
                        return lookPos;
                    }
                } else {
                    return lookPos;
                }
            }
        }

        return null;
    }

    public static boolean isBlockEmpty(BlockPos pos) {
        if (!mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            return false;
        }

        Box box = new Box(pos);
        for (Entity e : mc.world.getEntities()) {
            if (e instanceof LivingEntity && box.intersects(e.getBoundingBox())) {
                return false;
            }
        }

        return true;
    }

    public static void facePos(double x, double y, double z) {
        double diffX = x - mc.player.getX();
        double diffY = y - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = z - mc.player.getZ();

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        mc.player.setYaw(mc.player.getYaw() + MathHelper.wrapDegrees(yaw - mc.player.getYaw()));
        mc.player.setPitch(mc.player.getPitch() + MathHelper.wrapDegrees(pitch - mc.player.getPitch()));
    }

    public static void facePosPacket(double x, double y, double z) {
        double diffX = x - mc.player.getX();
        double diffY = y - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = z - mc.player.getZ();

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        if (!mc.player.hasVehicle()) {
            mc.player.headYaw = mc.player.getYaw() + MathHelper.wrapDegrees(yaw - mc.player.getYaw());
            mc.player.bodyYaw = mc.player.getYaw() + MathHelper.wrapDegrees(yaw - mc.player.getYaw());
            mc.player.renderPitch = mc.player.getPitch() + MathHelper.wrapDegrees(pitch - mc.player.getPitch());
        }

        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
                        mc.player.getYaw() + MathHelper.wrapDegrees(yaw - mc.player.getYaw()),
                        mc.player.getPitch() + MathHelper.wrapDegrees(pitch - mc.player.getPitch()), mc.player.isOnGround()));
    }

}
