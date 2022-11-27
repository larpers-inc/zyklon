package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import dev.vili.zyklon.util.InventoryUtils;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class AutoStaircase extends Module {
    public final BooleanSetting airPlace = new BooleanSetting("AirPlace", this, true);
    public final BooleanSetting autoJump = new BooleanSetting("AutoJump", this, false);

    public AutoStaircase() {
        super("AutoStaircase", "Automatically builds a staircase.", GLFW.GLFW_KEY_UNKNOWN, Category.MISC);
        this.addSettings(airPlace, autoJump);
    }

    @Override
    public void onDisable() {
        mc.options.forwardKey.setPressed(false);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) {
            setEnabled(false);
            return;
        }
        int blockSlot = InventoryUtils.getSlot(true, i ->  mc.player.getInventory().getStack(i).getItem() instanceof BlockItem);
        if (!mc.player.isOnGround() || blockSlot == -1) return;

        BlockPos pos = mc.player.getBlockPos().offset(mc.player.getMovementDirection());
        switch (mc.player.getMovementDirection()) {
            case NORTH -> mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 1), Direction.NORTH, pos, false));
            case EAST -> mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(new Vec3d(pos.getX(), pos.getY() + 0.5, pos.getZ() + 0.5), Direction.EAST, pos, false));
            case SOUTH -> mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ()), Direction.SOUTH, pos, false));
            case WEST -> mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(new Vec3d(pos.getX() + 1, pos.getY() + 0.5, pos.getZ() + 0.5), Direction.WEST, pos, false));
            default -> {}
        }
        if (mc.player.getBlockStateAtPos().getMaterial().isReplaceable()) {
            mc.options.forwardKey.setPressed(false);
            mc.player.getInventory().selectedSlot = blockSlot;
            if (!airPlace.isEnabled()) mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.of(pos.down()), Direction.DOWN, pos, false));
            else mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.of(pos), Direction.DOWN, pos, false));
        }
        if (!mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            if (!(mc.player.getInventory().getMainHandStack().getItem() instanceof BlockItem)) return;
            mc.options.forwardKey.setPressed(true);
            if (autoJump.isEnabled()) mc.player.jump();
        }
    }
}
