package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.TickEvent;
import me.vp.zyklon.event.events.WorldRenderEvent;
import me.vp.zyklon.eventbus.Subscribe;
import me.vp.zyklon.mixin.accessor.MinecraftClientAccessor;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.BooleanSetting;
import me.vp.zyklon.setting.settings.ModeSetting;
import me.vp.zyklon.util.RenderUtils;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class AirPlace extends Module {
    public final BooleanSetting highlight = new BooleanSetting("Highlight", this, true);
    public final ModeSetting mode = new ModeSetting("Mode", this, "Single", "Single", "Multi");

    boolean pressed;

    public AirPlace() {
        super("AirPlace", "Places blocks in the air.", GLFW.GLFW_KEY_UNKNOWN, Category.PLAYER);
        this.addSettings(highlight, mode);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        boolean isKeyUsePressed = mc.options.useKey.isPressed();

        if (!canBePlaced()) return;

        if (mode.is("Single")) {
            if (((MinecraftClientAccessor) mc).getItemUseCooldown() == 4 && isKeyUsePressed) {
                mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) mc.crosshairTarget, 0));
            }
        } else if (mode.is("Multi")) {
            if (!pressed && isKeyUsePressed) {
                mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) mc.crosshairTarget, 0));
                pressed = true;
            } else if (!isKeyUsePressed) {
                pressed = false;
            }
        }
    }

    @Subscribe
    public void onRender(WorldRenderEvent.Post event) {
        if (!canBePlaced()) return;

        BlockPos pos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();

        if (highlight.isEnabled()) {
            RenderUtils.draw3DBox(event.getMatrix(), new Box(pos), new Color(0, 10, 140), 0.05f);
        }
    }

    private boolean canBePlaced() {
        return mc.crosshairTarget instanceof BlockHitResult
                && mc.world.getBlockState(((BlockHitResult) mc.crosshairTarget).getBlockPos()).getMaterial().isReplaceable();
    }
}
