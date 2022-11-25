package dev.vili.zyklon.mixin;

import dev.vili.zyklon.Zyklon;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.render.occlusion.BlockOcclusionCache")
public class BlockOcclusionCacheMixin {
    @Inject(at = @At("HEAD"), method = "shouldDrawSide", cancellable = true, remap = false)
    private boolean xray(BlockState state, BlockView world, BlockPos pos, Direction side, CallbackInfoReturnable<Boolean> cir) {
        if (Zyklon.INSTANCE.moduleManager.getModule("Xray").isEnabled()) {
            boolean blockVisible = Zyklon.INSTANCE.xrayManager.isXrayBlock(state.getBlock());
            if (state.isOf(Blocks.BEDROCK)) {
                blockVisible=true;
            } else {
                cir.setReturnValue(blockVisible);
                return blockVisible;
            }
        }
        return true;
    }
}
