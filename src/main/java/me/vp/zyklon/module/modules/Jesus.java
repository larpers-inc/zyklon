package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.BlockShapeEvent;
import me.vp.zyklon.event.events.TickEvent;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.BooleanSetting;
import me.vp.zyklon.setting.settings.ModeSetting;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import org.lwjgl.glfw.GLFW;
import me.vp.zyklon.eventbus.Subscribe;

public class Jesus extends Module {

    public Jesus() {
        super("Jesus", "Walk on water like the holy jesus christ.", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.world == null || mc.player == null) return;

        Entity entity = mc.player.getRootVehicle();

        if (entity.isSneaking() || entity.fallDistance > 3f) return;

		if (isSubmerged(entity.getPos().add(0, 0.3, 0))) {
			entity.setVelocity(entity.getVelocity().x, 0.08, entity.getVelocity().z);
		} else if (isSubmerged(entity.getPos().add(0, 0.1, 0))) {
			entity.setVelocity(entity.getVelocity().x, 0.05, entity.getVelocity().z);
		} else if (isSubmerged(entity.getPos().add(0, 0.05, 0))) {
			entity.setVelocity(entity.getVelocity().x, 0.01, entity.getVelocity().z);
		} else if (isSubmerged(entity.getPos())) {
			entity.setVelocity(entity.getVelocity().x, -0.005, entity.getVelocity().z);
			entity.setOnGround(true);
		}
    }

    @Subscribe
    public void onBlockShape(BlockShapeEvent event) {
        if (!mc.world.getFluidState(event.getPos()).isEmpty()
			&& !mc.player.isSneaking()
			&& !mc.player.isTouchingWater()
			&& mc.player.getY() >= event.getPos().getY() + 0.9) {
            event.setShape(VoxelShapes.cuboid(0, 0, 0, 1, 0.9, 1));
        }
    }

    private boolean isSubmerged(Vec3d pos) {
        BlockPos bp = new BlockPos(pos);
		FluidState state = mc.world.getFluidState(bp);

		return !state.isEmpty() && pos.y - bp.getY() <= state.getHeight();
    }

}