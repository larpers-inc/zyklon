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
import net.minecraft.entity.player.PlayerEntity;
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

public class AutoBedbomb extends Module {
    public final NumberSetting delay = new NumberSetting("Delay", this, 4, 0.1, 10, 0.1);
    public final NumberSetting range = new NumberSetting("Range", this, 5, 1, 10, 1);
    public final BooleanSetting deathDisable = new BooleanSetting("DeathDisable", this, true);

    int ticksPassed;
    int currentSlot;
    public AutoBedbomb() {
        super("AutoBedbomb", "Automatically places beds on players and explodes them.", GLFW.GLFW_KEY_UNKNOWN, Category.COMBAT);
        this.addSettings(delay, range, deathDisable);
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
        if (mc.player == null || mc.world == null) {
            this.setEnabled(false);
            return;
        }

        if (mc.player.getHealth() <= 0 && deathDisable.isEnabled()) {
            this.setEnabled(false);
            return;
        }

        if (!mc.player.isAlive()) return;

        ticksPassed++;
        if (ticksPassed < delay.getValue())
            return;
        ticksPassed = 0;

        currentSlot = mc.player.getInventory().selectedSlot;
        List<Entity> targets = Streams.stream(mc.world.getEntities())
                .filter(e -> EntityUtils.isOtherServerPlayer(e))
                .filter(e -> e != mc.player || !EntityUtils.isFriend(e))
                .filter(e -> e.getBlockPos() != mc.player.getBlockPos())
                .filter(e -> mc.player.distanceTo(e) < range.getValue())
                .filter(e -> !((PlayerEntity) e).isDead())
                .filter(e -> (mc.world.getBlockState((e).getBlockPos()).getBlock() == Blocks.AIR || mc.world.getBlockState((e).getBlockPos()).getBlock() == Blocks.LAVA))
                .min((a, b) -> Float.compare(a.distanceTo(mc.player), b.distanceTo(mc.player))).stream().toList();

        if (targets.isEmpty()) return;

        Entity target = targets.get(0);
        if (target.isInvulnerable()) {
            ZLogger.warn("Target is invulnerable!");
            return;
        }

        ArrayList<Pair<BlockPos, Direction>> positions = new ArrayList<>();
        positions.add(new Pair<>(target.getBlockPos().north().up(), Direction.SOUTH));
        positions.add(new Pair<>(target.getBlockPos().east().up(), Direction.WEST));
        positions.add(new Pair<>(target.getBlockPos().south().up(), Direction.NORTH));
        positions.add(new Pair<>(target.getBlockPos().west().up(), Direction.EAST));
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
                if (direction == Direction.NORTH)
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(-180f, mc.player.getPitch(), true));
                if (direction == Direction.EAST)
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(-90f, mc.player.getPitch(), true));
                if (direction == Direction.SOUTH)
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(0f, mc.player.getPitch(), true));
                if (direction == Direction.WEST)
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(90f, mc.player.getPitch(), true));
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.of(blockPos), Direction.DOWN, blockPos, false));
            }
            if (mc.world.getBlockState(blockPos).getBlock() instanceof BedBlock)
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.of(blockPos), Direction.DOWN, blockPos, true));
            mc.player.getInventory().selectedSlot = currentSlot;
            break;
        }
    }
}
