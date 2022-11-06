package dev.vili.zyklon.mixin;

import dev.vili.zyklon.module.modules.CameraClip;
import dev.vili.zyklon.Zyklon;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {

    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    public void clipToSpace(double desiredCameraDistance, CallbackInfoReturnable<Double> cir) {
        CameraClip cameraClip = (CameraClip) Zyklon.INSTANCE.moduleManager.getModule("CameraClip");

        if (cameraClip != null && cameraClip.isEnabled()) {
            cir.setReturnValue(cameraClip.range.getValue());
        }
    }
}
