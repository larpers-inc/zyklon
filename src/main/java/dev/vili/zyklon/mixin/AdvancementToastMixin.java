package dev.vili.zyklon.mixin;

import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.module.modules.NoOverlay;
import net.minecraft.client.toast.AdvancementToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementToast.class)
public class AdvancementToastMixin {

    @Inject(method = "draw", at = @At("HEAD"), cancellable = true)
    public void draw(MatrixStack matrices, ToastManager manager, long startTime, CallbackInfoReturnable<Toast.Visibility> cir) {
        NoOverlay noOverlay = (NoOverlay) Zyklon.INSTANCE.moduleManager.getModule("NoOverlay");

        if (noOverlay != null && noOverlay.isEnabled() && noOverlay.toast.isEnabled()) {
            cir.setReturnValue(Toast.Visibility.HIDE);
        }
    }
}
