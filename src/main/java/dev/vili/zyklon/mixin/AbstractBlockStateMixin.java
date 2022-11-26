package dev.vili.zyklon.mixin;

import dev.vili.zyklon.Zyklon;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SideShapeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {

    @Inject(method = "isSideInvisible", at = @At("HEAD"), cancellable = true)
    private void isSideInvisible(BlockState state, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (Zyklon.INSTANCE.moduleManager.getModule("XRay").isEnabled() && Zyklon.INSTANCE.xrayManager.isXrayBlock(state.getBlock()))
            cir.setReturnValue(true);

        cir.setReturnValue(false);
    }

    @Inject(method = "isSideSolid", at = @At("HEAD"), cancellable = true)
    private void isSideSolid(BlockView world, BlockPos pos, Direction direction, SideShapeType shapeType, CallbackInfoReturnable<Boolean> cir) {
        if (Zyklon.INSTANCE.moduleManager.getModule("XRay").isEnabled() && Zyklon.INSTANCE.xrayManager.isXrayBlock(world.getBlockState(pos).getBlock()))
            cir.setReturnValue(true);

        cir.setReturnValue(false);
    }

    @Inject(method = "getLuminance", at = @At("HEAD"), cancellable = true)
    private void getLuminance(CallbackInfoReturnable<Integer> cir) {
        if (Zyklon.INSTANCE.moduleManager.getModule("XRay").isEnabled()) cir.setReturnValue(15);
    }
}
