package dev.vili.zyklon.mixin;

import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.module.modules.Nametags;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.vili.zyklon.Zyklon.mc;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {
    @Shadow
    @Final
    protected EntityRenderDispatcher dispatcher;

    @Inject(at = {@At("HEAD")}, method = {"renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"}, cancellable = true)
    private void onRenderLabelIfPresent(T entity, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
        Nametags nametags = (Nametags) Zyklon.INSTANCE.moduleManager.getModule("Nametags");

        if (nametags.health.isEnabled() && entity instanceof LivingEntity)
                text = nametags.addHealth(entity, text);
        if (nametags.ping.isEnabled() && entity instanceof PlayerEntity)
                text = nametags.addPing(entity, text);
        if (nametags.distance.isEnabled() && entity instanceof LivingEntity)
                text = nametags.addDistance(entity, text);
        if (nametags.gamemode.isEnabled() && entity instanceof PlayerEntity)
                text = nametags.addGamemode(entity, text);
        else if (!nametags.isEnabled()) return;

        ZRenderLabel(entity, text, matrixStack, vertexConsumerProvider, light);

        ci.cancel();
    }

    /* Credits to Wurst Client XOXO */
    protected void ZRenderLabel(T entity, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        double d = dispatcher.getSquaredDistanceToCamera(entity);

        if (d > 4096) return;

        Nametags nametags = (Nametags) Zyklon.INSTANCE.moduleManager.getModule("Nametags");

        boolean bl = !entity.isSneaky() || nametags.isEnabled();
        float f = entity.getHeight() + 0.5F;
        int j = "deadmau5".equals(text.getString()) ? - 10 : 0;

        matrixStack.push();
        matrixStack.translate(0.0D, f, 0.0D);
        matrixStack.multiply(dispatcher.getRotation());

        float scale = 0.025F;
        if (nametags.isEnabled()) {
            double distance = mc.player.distanceTo(entity);

            if (distance > 10)
                scale *= distance / 10;
        }

        matrixStack.scale(-scale, -scale, scale);

        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        float g = mc.options.getTextBackgroundOpacity(0.25F);
        int k = (int)(g * 255.0F) << 24;

        TextRenderer textRenderer = getTextRenderer();
        float h = -textRenderer.getWidth(text) / 2;

        textRenderer.draw(text.asOrderedText(), h, j, 553648127, false, matrix4f, vertexConsumerProvider, bl, k, i);

        if (bl) {
            textRenderer.draw(text.asOrderedText(), h, j, -1, false, matrix4f, vertexConsumerProvider, false, 0, i);
        }

        matrixStack.pop();
    }


    @Shadow
    public TextRenderer getTextRenderer() {
        return null;
    }
}
