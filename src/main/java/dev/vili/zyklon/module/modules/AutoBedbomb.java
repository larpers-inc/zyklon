package dev.vili.zyklon.module.modules;

import com.google.common.collect.Streams;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import dev.vili.zyklon.setting.settings.NumberSetting;
import dev.vili.zyklon.util.EntityUtils;
import dev.vili.zyklon.util.InventoryUtils;
import dev.vili.zyklon.util.ZLogger;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.BedItem;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AutoBedbomb extends Module {
    public final BooleanSetting players = new BooleanSetting("Players", this, true);
    public final BooleanSetting friends = new BooleanSetting("Friends", this, false);
    public final BooleanSetting hostiles = new BooleanSetting("Hostiles", this, false);
    public final BooleanSetting friendly = new BooleanSetting("Friendly", this, false);
    public final NumberSetting delay = new NumberSetting("Delay", this, 4, 0.1, 10, 0.1);
    public final NumberSetting range = new NumberSetting("Range", this, 5, 1, 10, 1);
    public final BooleanSetting deathDisable = new BooleanSetting("DeathDisable", this, true);

    int ticksPassed;
    int currentSlot;
    public AutoBedbomb() {
        super("AutoBedbomb", "Automatically places beds on players and explodes them.", GLFW.GLFW_KEY_UNKNOWN, Category.COMBAT);
        this.addSettings(players, friends, hostiles, friendly, delay, range, deathDisable);
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) return;
        ticksPassed = 0;
    }

    @Override
    public void onDisable() {
        ticksPassed = 0;
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) this.setEnabled(false);
        if (mc.player.getHealth() <= 0 && deathDisable.isEnabled()) this.setEnabled(false);
        if (!mc.world.getDimension().respawnAnchorWorks()) return;

        ticksPassed++;
        if (ticksPassed < delay.getValue()) return;
        ticksPassed = 0;

        currentSlot = mc.player.getInventory().selectedSlot;


        for (Entity entity : getTargets()) {
            if (entity.isInvulnerable()) ZLogger.warn("Target is invulnerable!");

            ArrayList<Pair<BlockPos, Direction>> positions = new ArrayList<>();
            positions.add(new Pair<>(entity.getBlockPos().north().up(), Direction.SOUTH));
            positions.add(new Pair<>(entity.getBlockPos().east().up(), Direction.WEST));
            positions.add(new Pair<>(entity.getBlockPos().south().up(), Direction.NORTH));
            positions.add(new Pair<>(entity.getBlockPos().west().up(), Direction.EAST));
            positions.sort(Comparator.comparing(object -> object.getLeft().getSquaredDistance(mc.player.getX(), mc.player.getY(), mc.player.getZ())));
            for (Pair<BlockPos, Direction> pair : positions) {
                BlockPos blockPos = pair.getLeft();
                Direction direction = pair.getRight();
                currentSlot = mc.player.getInventory().selectedSlot;
                int bedSlot = InventoryUtils.getSlot(true, i -> mc.player.getInventory().getStack(i).getItem() instanceof BedItem);
                if (bedSlot == -1) return;

                mc.player.getInventory().selectedSlot = bedSlot;
                if (!(mc.world.getBlockState(blockPos)).getMaterial().isReplaceable()) continue;
                if (mc.world.getBlockState(blockPos.offset(direction)).getBlock() instanceof BedBlock)
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.of(blockPos.offset(direction)), Direction.DOWN, blockPos.offset(direction), true));
                if (!(mc.world.getBlockState(blockPos).getBlock() instanceof BedBlock)) {
                    if (direction == Direction.NORTH) mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(-180f, mc.player.getPitch(), true));
                    if (direction == Direction.EAST) mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(-90f, mc.player.getPitch(), true));
                    if (direction == Direction.SOUTH) mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(0f, mc.player.getPitch(), true));
                    if (direction == Direction.WEST) mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(90f, mc.player.getPitch(), true));
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.of(blockPos), Direction.DOWN, blockPos, false));
                }
                if (mc.world.getBlockState(blockPos).getBlock() instanceof BedBlock)
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.of(blockPos), Direction.DOWN, blockPos, true));
                mc.player.getInventory().selectedSlot = currentSlot;
                break;
            }
        }
    }

    private List<Entity> getTargets() {
        Stream<Entity> targets;
        targets = Streams.stream(mc.world.getEntities());
        Comparator<Entity> comparator;
        comparator = Comparator.comparing(mc.player::distanceTo);

        return targets
                .filter(e -> EntityUtils.isAttackable(e, true))
                .filter(e -> (EntityUtils.isOtherServerPlayer(e) && players.isEnabled())
                        || (EntityUtils.isFriend(e) && friends.isEnabled())
                        || (EntityUtils.isMob(e) && hostiles.isEnabled())
                        || (EntityUtils.isAnimal(e) && friendly.isEnabled()))
                .filter(e -> (mc.world.getBlockState((e).getBlockPos()).getBlock() == Blocks.AIR
                        || mc.world.getBlockState((e).getBlockPos()).getBlock() == Blocks.LAVA))
                .filter(e -> mc.player.distanceTo(e) < range.getValue())
                .sorted(comparator)
                .limit(1L)
                .collect(Collectors.toList());
    }
}
