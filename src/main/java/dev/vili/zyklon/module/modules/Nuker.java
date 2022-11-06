package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.event.events.WorldRenderEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import dev.vili.zyklon.setting.settings.NumberSetting;
import dev.vili.zyklon.util.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class Nuker extends Module {
    public final BooleanSetting render = new BooleanSetting("RenderBlocks", this, true);
    public final NumberSetting radius = new NumberSetting("Radius", this, 5, 1, 10, 1);
    public Nuker() {
        super("Nuker", "Breaks blocks around you.", GLFW.GLFW_KEY_UNKNOWN, Category.PLAYER);
        this.addSettings(render, radius);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        int rad = (int) radius.getValue();
        for (int x = -rad; x < rad; x++) {
            for (int y = rad; y > -rad; y--) {
                for (int z = -rad; z < rad; z++) {
                    BlockPos blockpos = new BlockPos(mc.player.getBlockX() + x, mc.player.getBlockY() + y, mc.player.getBlockZ() + z);
                    Block block = mc.world.getBlockState(blockpos).getBlock();
                    if (block == Blocks.AIR)
                        continue;
                    mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockpos, Direction.NORTH));
                    mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockpos, Direction.NORTH));
                }
            }
        }
    }

    @Subscribe
    public void onRender(WorldRenderEvent.Post event) {
        if (!render.isEnabled()) return;

        int rad = (int) radius.getValue();
        for (int x = -rad; x < rad; x++) {
            for (int y = rad; y > -rad; y--) {
                for (int z = -rad; z < rad; z++) {
                    BlockPos blockpos = new BlockPos(mc.player.getBlockX() + x, mc.player.getBlockY() + y, mc.player.getBlockZ() + z);
                    Block block = mc.world.getBlockState(blockpos).getBlock();

                    if (block == Blocks.AIR || block == Blocks.WATER || block == Blocks.LAVA)
                        continue;

                    RenderUtils.draw3DBox(event.getMatrix(), new Box(blockpos), new Color(255, 0, 0), 0.1f);
                }
            }
        }
    }
}