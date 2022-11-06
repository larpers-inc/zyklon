package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.WorldRenderEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.util.RenderUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class BlockHighlight extends Module {

    public BlockHighlight() {
        super("BlockHighlight", "Renders a outline around blocks.", GLFW.GLFW_KEY_UNKNOWN, Category.RENDER);
    }

    @Subscribe
    public void onRender(WorldRenderEvent.Post event) {
        if (mc.world == null || mc.player == null) return;
        if (mc.crosshairTarget == null || mc.crosshairTarget.getType() == HitResult.Type.ENTITY) return;

        BlockPos pos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
        BlockState state = mc.world.getBlockState(pos);

        if (state.getMaterial() == Material.AIR || !mc.world.getWorldBorder().contains(pos)) return;

        RenderUtils.drawOutline(event.getMatrix(), new Box(pos), new Color(0, 10, 140), 0.05f);
    }
}