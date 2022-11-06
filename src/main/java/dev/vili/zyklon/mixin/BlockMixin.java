package dev.vili.zyklon.mixin;

import dev.vili.zyklon.Zyklon;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(at = {@At("HEAD")}, method = {
            "shouldDrawSide(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/minecraft/util/math/BlockPos;)Z"}, cancellable = true)
    private static void onShouldDrawSide(BlockState state, BlockView world, BlockPos pos, Direction direction, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        if (Zyklon.INSTANCE.moduleManager.getModule("XRay").isEnabled()) {
            cir.setReturnValue(Zyklon.INSTANCE.xrayManager.isXrayBlock(state.getBlock()));
        }
    }
}
