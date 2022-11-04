package me.vp.zyklon.mixin;

import me.vp.zyklon.Zyklon;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {

    @Inject(at = {@At("HEAD")}, method = {"getAmbientOcclusionLightLevel(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F"}, cancellable = true)
    public void onGetAmbientOcclusionLightLevel(BlockState state, BlockView view, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (!Zyklon.INSTANCE.moduleManager.getModule("XRay").isEnabled())
            return;
        cir.setReturnValue(1F);
    }
}
