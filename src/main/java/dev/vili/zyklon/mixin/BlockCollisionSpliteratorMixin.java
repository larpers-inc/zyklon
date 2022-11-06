package dev.vili.zyklon.mixin;

import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.event.events.BlockShapeEvent;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockCollisionSpliterator;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockCollisionSpliterator.class)
public class BlockCollisionSpliteratorMixin {

    @Redirect(method = "computeNext", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;"))
	private VoxelShape computeNext_getCollisionShape(BlockState blockState, BlockView world, BlockPos pos, ShapeContext context) {
		VoxelShape shape = blockState.getCollisionShape(world, pos, context);
		BlockShapeEvent event = new BlockShapeEvent((BlockState) blockState, pos, shape);
		Zyklon.INSTANCE.EVENT_BUS.post(event);

		if (event.isCancelled()) {
			return VoxelShapes.empty();
		}

		return event.getShape();
	}
}