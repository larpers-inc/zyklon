package me.vp.zyklon.mixin;

import me.vp.zyklon.Zyklon;
import net.minecraft.client.ClientBrandRetriever;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientBrandRetriever.class)
public class ClientBrandRetrieverMixin {
    @Inject(method = "getClientModName", at = @At("HEAD"), cancellable = true, remap = false)
    private static void getClientModName(CallbackInfoReturnable<String> cir) {
        if (Zyklon.INSTANCE.moduleManager.getModule("VanillaSpoof").isEnabled())
            cir.setReturnValue("vanilla");
    }
}
