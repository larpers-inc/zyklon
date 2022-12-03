package dev.vili.zyklon.mixin;

import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.module.modules.Jesus;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {

    @Inject(at = {@At("HEAD")}, method = {"canWalkOnPowderSnow(Lnet/minecraft/entity/Entity;)Z"}, cancellable = true)
    private static void onCanWalkOnPowderSnow(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        Jesus jesus = (Jesus) Zyklon.INSTANCE.moduleManager.getModule("jesus");

        if (!jesus.isEnabled() && !jesus.powderSnow.isEnabled()) return;
        if (entity != Zyklon.mc.player) return;

        cir.setReturnValue(true);
    }
}
