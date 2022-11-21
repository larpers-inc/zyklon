package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import dev.vili.zyklon.setting.settings.NumberSetting;
import dev.vili.zyklon.util.InventoryUtils;
import dev.vili.zyklon.util.WorldUtils;
import net.minecraft.block.*;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.gen.feature.TreeFeature;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/* Credits @BleachDev , My beloved */
public class AutoFarm extends Module {
    public final NumberSetting range = new NumberSetting("Range", this, 3.5, 4, 6, 0.1);
    public final BooleanSetting till = new BooleanSetting("Till", this, false);
    public final BooleanSetting replant = new BooleanSetting("Replant", this, true);
    public final BooleanSetting harvest = new BooleanSetting("Harvest", this, true);
    public final BooleanSetting bonemeal = new BooleanSetting("Bonemeal", this, false);

    private final Map<BlockPos, Integer> mossMap = new HashMap<>();
    public AutoFarm() {
        super("AutoFarm", "Automatically does the farming stuff for you.", GLFW.GLFW_KEY_UNKNOWN, Category.MISC);
        this.addSettings(range, till, replant, harvest, bonemeal);
    }

    @Override
    public void onDisable() {
        mossMap.clear();
    }

    @Subscribe
    public void onTick(TickEvent event) {
        mossMap.entrySet().removeIf(e -> e.setValue(e.getValue() - 1) == 0);

        double r = range.getValue();
        int ceilRange = MathHelper.ceil(r);

        // Special case for moss to maximize efficiency
        if (bonemeal.isEnabled()) {
            int slot = InventoryUtils.getSlot(true, i -> mc.player.getInventory().getStack(i).getItem() == Items.BONE_MEAL);
            if (slot != -1) {
                BlockPos bestBlock = BlockPos.streamOutwards(new BlockPos(mc.player.getEyePos()), ceilRange, ceilRange, ceilRange)
                        .filter(b -> mc.player.getEyePos().distanceTo(Vec3d.ofCenter(b)) <= r && !mossMap.containsKey(b))
                        .map(b -> Pair.of(b.toImmutable(), getMossSpots(b)))
                        .filter(p -> p.getRight() > 10)
                        .map(Pair::getLeft)
                        .min(Comparator.reverseOrder()).orElse(null);

                if (bestBlock != null) {
                    if (!mc.world.isAir(bestBlock.up())) {
                        mc.interactionManager.updateBlockBreakingProgress(bestBlock.up(), Direction.UP);
                    }

                    Hand hand = InventoryUtils.selectSlot(slot);
                    mc.interactionManager.interactBlock(mc.player, hand,
                            new BlockHitResult(Vec3d.ofCenter(bestBlock, 1), Direction.UP, bestBlock, false));
                    mossMap.put(bestBlock, 100);
                    return;
                }
            }
        }

        for (BlockPos pos: BlockPos.iterateOutwards(new BlockPos(mc.player.getEyePos()), ceilRange, ceilRange, ceilRange)) {
            if (mc.player.getEyePos().distanceTo(Vec3d.ofCenter(pos)) > r)
                continue;

            BlockState state = mc.world.getBlockState(pos);
            Block block = state.getBlock();
            if (till.isEnabled() && canTill(block) && mc.world.isAir(pos.up())) {
                if (BlockPos.stream(pos.getX() - 4, pos.getY(), pos.getZ() - 4, pos.getX() + 4, pos.getY(), pos.getZ() + 4).anyMatch(
                        b -> mc.world.getFluidState(b).isIn(FluidTags.WATER))) {
                    Hand hand = InventoryUtils.selectSlot(true, i -> mc.player.getInventory().getStack(i).getItem() instanceof HoeItem);

                    if (hand != null) {
                        mc.interactionManager.interactBlock(mc.player, hand, new BlockHitResult(Vec3d.ofCenter(pos, 1), Direction.UP, pos, false));
                        return;
                    }
                }
            }

            if (harvest.isEnabled()) {
                if ((block instanceof CropBlock && ((CropBlock) block).isMature(state))
                        || block instanceof GourdBlock
                        || block instanceof NetherWartBlock && state.get(NetherWartBlock.AGE) >= 3
                        || block instanceof CocoaBlock && state.get(CocoaBlock.AGE) >= 2
                        || block instanceof SweetBerryBushBlock && state.get(SweetBerryBushBlock.AGE) >= 3
                        || shouldHarvestTallCrop(pos, block, SugarCaneBlock.class)
                        || shouldHarvestTallCrop(pos, block, CactusBlock.class)) {
                    mc.interactionManager.updateBlockBreakingProgress(pos, Direction.UP);
                    return;
                }
            }

            if (replant.isEnabled() && mc.world.getOtherEntities(null, new Box(pos.up()), EntityPredicates.VALID_LIVING_ENTITY).isEmpty()) {
                if (block instanceof FarmlandBlock && mc.world.isAir(pos.up())) {
                    int slot = InventoryUtils.getSlot(true, i -> {
                        Item item = mc.player.getInventory().getStack(i).getItem();

                        if (item == Items.WHEAT_SEEDS || item == Items.CARROT || item == Items.POTATO || item == Items.BEETROOT_SEEDS) return true;
                        return item == Items.PUMPKIN_SEEDS || item == Items.MELON_SEEDS;
                    });

                    if (slot != -1) {
                        WorldUtils.placeBlock(pos.up(), slot, 0, false, false, true);
                        return;
                    }
                }

                if (block instanceof SoulSandBlock && mc.world.isAir(pos.up())) {
                    int slot = InventoryUtils.getSlot(true, i -> mc.player.getInventory().getStack(i).getItem() == Items.NETHER_WART);

                    if (slot != -1) {
                        WorldUtils.placeBlock(pos.up(), slot, 0, false, false, true);
                        return;
                    }
                }
            }

            if (bonemeal.isEnabled()) {
                int slot = InventoryUtils.getSlot(true, i -> mc.player.getInventory().getStack(i).getItem() == Items.BONE_MEAL);

                if (slot != -1) {
                    if (block instanceof CropBlock && !((CropBlock) block).isMature(state)
                            ||block instanceof StemBlock && state.get(StemBlock.AGE) < StemBlock.MAX_AGE
                            ||block instanceof CocoaBlock && state.get(CocoaBlock.AGE) < 2
                            ||block instanceof SweetBerryBushBlock && state.get(SweetBerryBushBlock.AGE) < 3
                            || block instanceof MushroomPlantBlock
                            || block instanceof SaplingBlock || block instanceof AzaleaBlock
                            && canPlaceSapling(pos)) {
                        Hand hand = InventoryUtils.selectSlot(slot);
                        mc.interactionManager.interactBlock(mc.player, hand,
                                new BlockHitResult(Vec3d.ofCenter(pos, 1), Direction.UP, pos, false));
                        return;
                    }
                }
            }
        }
    }

    private boolean shouldHarvestTallCrop(BlockPos pos, Block posBlock, Class<? extends Block> blockClass) {
        return posBlock.getClass().equals(blockClass)
                && mc.world.getBlockState(pos.down()).getBlock().getClass().equals(blockClass)
                && !mc.world.getBlockState(pos.down(2)).getBlock().getClass().equals(blockClass);
    }

    private int getMossSpots(BlockPos pos) {
        if (mc.world.getBlockState(pos).getBlock() != Blocks.MOSS_BLOCK
                || mc.world.getBlockState(pos.up()).getHardness(mc.world, pos) != 0f) {
            return 0;
        }

        return (int) BlockPos.streamOutwards(pos, 3, 4, 3)
                .filter(b -> isMossGrowableOn(mc.world.getBlockState(b).getBlock()) && mc.world.isAir(b.up()))
                .count();
    }

    private boolean isMossGrowableOn(Block block) {
        return block == Blocks.STONE || block == Blocks.GRANITE || block == Blocks.ANDESITE || block == Blocks.DIORITE
                || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.MYCELIUM || block == Blocks.GRASS_BLOCK
                || block == Blocks.PODZOL || block == Blocks.ROOTED_DIRT;
    }

    private boolean canPlaceSapling(BlockPos pos) {
        return BlockPos.stream(pos.getX() - 1, pos.getY() + 1, pos.getZ() - 1, pos.getX() + 1, pos.getY() + 5, pos.getZ() + 1)
                .allMatch(b -> TreeFeature.canReplace(mc.world, b));
    }

    private boolean canTill(Block block) {
        return block == Blocks.DIRT || block == Blocks.GRASS_BLOCK || block == Blocks.COARSE_DIRT
                || block == Blocks.ROOTED_DIRT || block == Blocks.DIRT_PATH;
    }
}
