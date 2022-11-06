package dev.vili.zyklon.mixin;

import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.module.modules.Freecam;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "shouldRenderBlockOutline", at = @At("HEAD"), cancellable = true)
    public void shouldRenderBlockOutline(CallbackInfoReturnable<Boolean> cir) {
        Freecam freecam = (Freecam) Zyklon.INSTANCE.moduleManager.getModule("Freecam");

        if (freecam != null && freecam.isEnabled()) cir.setReturnValue(false);
    }
}
