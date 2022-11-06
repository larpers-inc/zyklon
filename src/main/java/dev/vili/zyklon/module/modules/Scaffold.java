package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import dev.vili.zyklon.util.WorldUtils;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.glfw.GLFW;

public class Scaffold extends Module {

    public Scaffold() {
        super("Scaffold", "Places blocks under you.", GLFW.GLFW_KEY_UNKNOWN, Category.PLAYER);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.world == null || mc.player == null) return;
        int currentSlot = mc.player.getInventory().selectedSlot;

        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = mc.player.getInventory().getStack(slot);
            if (stack.getItem() instanceof BlockItem) {
                Direction dir;
                if (mc.player.getMovementDirection() == Direction.NORTH) dir = Direction.NORTH;
                else if (mc.player.getMovementDirection() == Direction.EAST) dir = Direction.EAST;
                else if (mc.player.getMovementDirection() == Direction.SOUTH) dir = Direction.SOUTH;
                else dir = Direction.WEST;

                if (mc.world.getBlockState(mc.player.getBlockPos().offset(dir).offset(Direction.DOWN)).isOf(Blocks.AIR)) {
                    mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
                    WorldUtils.placeBlock(mc.player.getBlockPos(), Direction.DOWN);
                    WorldUtils.placeBlock(mc.player.getBlockPos().offset(dir), Direction.DOWN);
                    mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(currentSlot));
                }
            }
        }
    }
}

