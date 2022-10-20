package me.vp.zyklon.mixin;

import me.vp.zyklon.Zyklon;
import me.vp.zyklon.module.modules.NoRender;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantingTableBlockEntityRenderer.class)
public class EnchantingTableBlockEntityRendererMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(CallbackInfo ci) {
        NoRender noRender = (NoRender) Zyklon.INSTANCE.moduleManager.getModule("NoRender");

        if (noRender != null && noRender.isEnabled() && noRender.enchantmentTable.isEnabled()) ci.cancel();
    }
}
