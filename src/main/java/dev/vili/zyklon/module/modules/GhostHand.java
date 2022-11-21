package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.util.WorldUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

/* Credits @BleachDev */
public class GhostHand extends Module {

    public GhostHand() {
        super("GhostHand", "Open containers trough walls.", GLFW.GLFW_KEY_UNKNOWN, Category.PLAYER);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (!mc.options.useKey.isPressed() || mc.player.isSneaking())
            return;

        // Return if we are looking at any block entities
        BlockPos lookingPos = new BlockPos(mc.player.raycast(4.25, mc.getTickDelta(), false).getPos());
        for (BlockEntity b : WorldUtils.getBlockEntities()) {
            if (lookingPos.equals(b.getPos())) {
                return;
            }
        }

        Set<BlockPos> posList = new HashSet<>();

        Vec3d nextPos = new Vec3d(0, 0, 0.1)
                .rotateX(-(float) Math.toRadians(mc.player.getPitch()))
                .rotateY(-(float) Math.toRadians(mc.player.getYaw()));

        for (int i = 1; i < 50; i++) {
            BlockPos curPos = new BlockPos(mc.player.getCameraPosVec(mc.getTickDelta()).add(nextPos.multiply(i)));
            if (!posList.contains(curPos)) {
                posList.add(curPos);

                for (BlockEntity b : WorldUtils.getBlockEntities()) {
                    if (b.getPos().equals(curPos)) {
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND,
                                new BlockHitResult(Vec3d.ofCenter(curPos, 1), Direction.UP, curPos, true));
                        return;
                    }
                }
            }
        }
    }
}
