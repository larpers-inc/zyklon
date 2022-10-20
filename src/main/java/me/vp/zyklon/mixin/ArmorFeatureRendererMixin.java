package me.vp.zyklon.mixin;

import me.vp.zyklon.Zyklon;
import me.vp.zyklon.module.modules.NoRender;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public class ArmorFeatureRendererMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public <T extends LivingEntity> void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity,
                                                float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        NoRender noRender = (NoRender) Zyklon.INSTANCE.moduleManager.getModule("NoRender");

        if (noRender != null && noRender.isEnabled() && noRender.armor.isEnabled()) ci.cancel();
    }
}
