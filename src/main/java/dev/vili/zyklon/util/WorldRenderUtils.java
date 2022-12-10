package dev.vili.zyklon.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class WorldRenderUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    /** A Pointer to RenderSystem.shaderLightDirections **/
    private static final Vector3f[] shaderLight;

    static {
        try {
            shaderLight = (Vector3f[]) FieldUtils.getField(RenderSystem.class, "shaderLightDirections", true).get(null);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /** Draws text in the world. **/
    public static void drawText(Text text, double x, double y, double z, double scale, boolean shadow) {
        drawText(text, x, y, z, 0, 0, scale, shadow);
    }

    /** Draws text in the world. **/
    public static void drawText(Text text, double x, double y, double z, double offX, double offY, double scale, boolean fill) {
        MatrixStack matrices = matrixFrom(x, y, z);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        matrices.translate(offX, offY, 0);
        matrices.scale(-0.025f * (float) scale, -0.025f * (float) scale, 1);
        matrices.multiply(new Quaternionf().setAngleAxis(1, 0, 0, 180));
        matrices.multiply(new Quaternionf().setAngleAxis(1, 0, 1, 0));

        int halfWidth = mc.textRenderer.getWidth(text) / 2;

        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());

        if (fill) {
            int opacity = (int) (MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F) * 255.0F) << 24;
            mc.textRenderer.draw(text, -halfWidth, 0f, 553648127, false, matrices.peek().getPositionMatrix(), immediate, true, opacity, 0xf000f0);
            immediate.draw();
        } else {
            matrices.push();
            matrices.translate(1, 1, 0);
            mc.textRenderer.draw(text.copy(), -halfWidth, 0f, 0x202020, false, matrices.peek().getPositionMatrix(), immediate, true, 0, 0xf000f0);
            immediate.draw();
            matrices.pop();
        }

        mc.textRenderer.draw(text, -halfWidth, 0f, -1, false, matrices.peek().getPositionMatrix(), immediate, true, 0, 0xf000f0);
        immediate.draw();

        RenderSystem.disableBlend();
    }

    /** Draws a 2D gui items somewhere in the world. **/
    public static void drawGuiItem(double x, double y, double z, double offX, double offY, double scale, ItemStack item) {
        if (item.isEmpty()) return;
        MatrixStack matrices = matrixFrom(x, y, z);
        matrices.translate(offX, offY, 0);
        matrices.scale((float) scale, (float) scale, 0.001f);
        matrices.multiply(new Quaternionf().setAngleAxis(1, 0, 0, 180));
        matrices.multiply(new Quaternionf().setAngleAxis(1, 0, 1, 0));

        mc.getBufferBuilders().getEntityVertexConsumers().draw();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Vector3f[] currentLight = shaderLight.clone();
        DiffuseLighting.disableGuiDepthLighting();

        mc.getItemRenderer().renderItem(item, ModelTransformation.Mode.GUI, 0xF000F0,
                OverlayTexture.DEFAULT_UV, matrices, mc.getBufferBuilders().getEntityVertexConsumers(), 0);

        mc.getBufferBuilders().getEntityVertexConsumers().draw();

        RenderSystem.setShaderLights(currentLight[0], currentLight[1]);
        RenderSystem.disableBlend();
    }

    public static MatrixStack matrixFrom(double x, double y, double z) {
        float cameraX = (float) mc.gameRenderer.getCamera().getPos().x;
        float cameraY = (float) mc.gameRenderer.getCamera().getPos().y;
        float cameraZ = (float) mc.gameRenderer.getCamera().getPos().z;
        float endX = (float) (x - cameraX);
        float endY = (float) (y - cameraY);
        float endZ = (float) (z - cameraZ);

        MatrixStack matrices = new MatrixStack();

        matrices.translate(endX, endY, endZ);
        matrices.multiply(mc.gameRenderer.getCamera().getRotation());

        return matrices;
    }
}
