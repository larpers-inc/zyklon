package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.WorldRenderEvent;
import me.vp.zyklon.eventbus.Subscribe;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.util.RenderUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.util.hit.BlockHitResult;
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
        if (mc.crosshairTarget == null) return;

        BlockPos pos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
        BlockState state = mc.world.getBlockState(pos);

        if (state.getMaterial() == Material.AIR || !mc.world.getWorldBorder().contains(pos)) return;

        RenderUtils.drawOutline(event.getMatrix(), new Box(pos), new Color(0, 10, 140), 0.1f);
    }
}