package dev.vili.zyklon.mixin;

import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.module.modules.Reach;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(at = {@At("HEAD")}, method = {"getReachDistance()F"}, cancellable = true)
    private void onGetReachDistance(CallbackInfoReturnable<Float> ci) {
        Reach reach = (Reach) Zyklon.INSTANCE.moduleManager.getModule("Reach");
        if (reach.isEnabled())
            ci.setReturnValue((float) reach.range.getValue());
    }

    @Inject(at = {@At("HEAD")}, method = {"hasExtendedReach()Z"}, cancellable = true)
    private void hasExtendedReach(CallbackInfoReturnable<Boolean> cir) {
        Reach reach = (Reach) Zyklon.INSTANCE.moduleManager.getModule("Reach");
        if (reach.isEnabled())
            cir.setReturnValue(true);
    }
}
