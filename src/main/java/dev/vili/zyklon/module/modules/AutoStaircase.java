package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class AutoStaircase extends Module {
    public final BooleanSetting airPlace = new BooleanSetting("AirPlace", this, true);

    public AutoStaircase() {
        super("AutoStaircase", "Automatically builds a staircase.", GLFW.GLFW_KEY_UNKNOWN, Category.MISC);
        this.addSettings(airPlace);
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

        if (!mc.player.isOnGround() || !(mc.player.getInventory().getMainHandStack().getItem() instanceof BlockItem)) return;
        BlockPos pos = mc.player.getBlockPos().offset(mc.player.getMovementDirection());
        switch (mc.player.getMovementDirection()) {
            case NORTH:
                mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ() - 1));
                break;
            case EAST:
                mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(mc.player.getX() + 1, mc.player.getY(), mc.player.getZ()));
                break;
            case SOUTH:
                mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ() + 1));
                break;
            case WEST:
                mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(mc.player.getX() - 1, mc.player.getY(), mc.player.getZ()));
                break;
            default:
                break;
        }
        if (mc.player.getBlockStateAtPos().getMaterial().isReplaceable()) {
            mc.options.forwardKey.setPressed(false);
            if (!airPlace.isEnabled()) mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.of(pos.down()), Direction.DOWN, pos, false));
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.of(pos), Direction.DOWN, pos, false));
            mc.player.swingHand(Hand.MAIN_HAND);
        }
        if (!mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            mc.options.forwardKey.setPressed(true);
            mc.player.jump();
        }
    }
}
