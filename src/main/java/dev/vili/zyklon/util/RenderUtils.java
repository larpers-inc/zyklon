package dev.vili.zyklon.util;


import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;

import java.awt.*;

public class RenderUtils {
    public static final MinecraftClient mc = MinecraftClient.getInstance();

    /* TODO: rewrite */
    public static void draw3DBox(MatrixStack matrixStack, Box box, Color color, float alpha) {
        float minX = (float) (box.minX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float minY = (float) (box.minY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float minZ = (float) (box.minZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());
        float maxX = (float) (box.maxX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float maxY = (float) (box.maxY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float maxZ = (float) (box.maxZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        setup3D();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(color.getRed(), color.getGreen(), color.getBlue(), 1.0f);

        bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION);
        {
            bufferBuilder.vertex(matrix, minX, minX, minZ).next();
            bufferBuilder.vertex(matrix, maxX, minY, minZ).next();

            bufferBuilder.vertex(matrix, maxX, minY, minZ).next();
            bufferBuilder.vertex(matrix, maxX, minY, maxZ).next();

            bufferBuilder.vertex(matrix, maxX, minY, maxZ).next();
            bufferBuilder.vertex(matrix, minX, minY, maxZ).next();

            bufferBuilder.vertex(matrix, minX, minY, maxZ).next();
            bufferBuilder.vertex(matrix, minX, minY, minZ).next();

            bufferBuilder.vertex(matrix, minX, minY, minZ).next();
            bufferBuilder.vertex(matrix, minX, maxY, minZ).next();

            bufferBuilder.vertex(matrix, maxX, minY, minZ).next();
            bufferBuilder.vertex(matrix, maxX, maxY, minZ).next();

            bufferBuilder.vertex(matrix, maxX, minY, maxZ).next();
            bufferBuilder.vertex(matrix, maxX, maxY, maxZ).next();

            bufferBuilder.vertex(matrix, minX, minY, maxZ).next();
            bufferBuilder.vertex(matrix, minX, maxY, maxZ).next();

            bufferBuilder.vertex(matrix, minX, maxY, minZ).next();
            bufferBuilder.vertex(matrix, maxX, maxY, minZ).next();

            bufferBuilder.vertex(matrix, maxX, maxY, minZ).next();
            bufferBuilder.vertex(matrix, maxX, maxY, maxZ).next();

            bufferBuilder.vertex(matrix, maxX, maxY, maxZ).next();
            bufferBuilder.vertex(matrix, minX, maxY, maxZ).next();

            bufferBuilder.vertex(matrix, minX, maxY, maxZ).next();
            bufferBuilder.vertex(matrix, minX, maxY, minZ).next();
        }
        tessellator.draw();
        clean3D();

        setup3D();
        RenderSystem.setShaderColor(color.getRed(), color.getGreen(), color.getBlue(), alpha);

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        {
            bufferBuilder.vertex(matrix, minX, minY, minZ).next();
            bufferBuilder.vertex(matrix, maxX, minY, minZ).next();
            bufferBuilder.vertex(matrix, maxX, minY, maxZ).next();
            bufferBuilder.vertex(matrix, minX, minY, maxZ).next();

            bufferBuilder.vertex(matrix, minX, maxY, minZ).next();
            bufferBuilder.vertex(matrix, minX, maxY, maxZ).next();
            bufferBuilder.vertex(matrix, maxX, maxY, maxZ).next();
            bufferBuilder.vertex(matrix, maxX, maxY, minZ).next();

            bufferBuilder.vertex(matrix, minX, minY, minZ).next();
            bufferBuilder.vertex(matrix, minX, maxY, minZ).next();
            bufferBuilder.vertex(matrix, maxX, maxY, minZ).next();
            bufferBuilder.vertex(matrix, maxX, minY, minZ).next();

            bufferBuilder.vertex(matrix, maxX, minY, minZ).next();
            bufferBuilder.vertex(matrix, maxX, maxY, minZ).next();
            bufferBuilder.vertex(matrix, maxX, maxY, maxZ).next();
            bufferBuilder.vertex(matrix, maxX, minY, maxZ).next();

            bufferBuilder.vertex(matrix, minX, minY, maxZ).next();
            bufferBuilder.vertex(matrix, maxX, minY, maxZ).next();
            bufferBuilder.vertex(matrix, maxX, maxY, maxZ).next();
            bufferBuilder.vertex(matrix, minX, maxY, maxZ).next();

            bufferBuilder.vertex(matrix, minX, minY, minZ).next();
            bufferBuilder.vertex(matrix, minX, minY, maxZ).next();
            bufferBuilder.vertex(matrix, minX, maxY, maxZ).next();
            bufferBuilder.vertex(matrix, minX, maxY, minZ).next();
        }
        tessellator.draw();
        clean3D();
    }

    public static void drawOutlineBox(MatrixStack stack, Box box, Color color, float alpha) {
        float minX = (float) (box.minX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float minY = (float) (box.minY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float minZ = (float) (box.minZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());
        float maxX = (float) (box.maxX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float maxY = (float) (box.maxY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float maxZ = (float) (box.maxZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        setup3D();

        RenderSystem.setShaderColor(color.getRed(), color.getGreen(), color.getBlue(), 1.0f);
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesShader);
        RenderSystem.defaultBlendFunc();

        bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);

        WorldRenderer.drawBox(stack, bufferBuilder, minX, minY, minZ, maxX, maxY, maxZ, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        tessellator.draw();
        clean3D();
    }

    public static void draw3DLine(MatrixStack matrixStack, Vec3d start, Vec3d end, Color color) {
        float startX = (float) start.x;
        float startY = (float) start.y;
        float startZ = (float) start.z;
        float endX = (float) (end.x - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float endY = (float) (end.y - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float endZ = (float) (end.z - mc.getEntityRenderDispatcher().camera.getPos().getZ());

        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        setup3D();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(color.getRed(), color.getGreen(), color.getBlue(), 1.0f);

        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);
        {
            bufferBuilder.vertex(matrix, startX, startY, startZ).next();
            bufferBuilder.vertex(matrix, endX, endY, endZ).next();
            bufferBuilder.vertex(matrix, startX, startY, startZ).next();
            bufferBuilder.vertex(matrix, endX, endY, endZ).next();
        }
        tessellator.draw();
        clean3D();
    }


    public static void drawRect(float x, float y, float w, float h, Color color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        setup3D();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(color.getRed(), color.getGreen(), color.getBlue(), 1.0f);
        RenderSystem.defaultBlendFunc();

        bufferbuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        {
            bufferbuilder.vertex(x, h, 0.0D).color(color.getRed(), color.getGreen(), color.getBlue(), 1.0f).next();
            bufferbuilder.vertex(w, h, 0.0D).color(color.getRed(), color.getGreen(), color.getBlue(), 1.0f).next();
            bufferbuilder.vertex(w, y, 0.0D).color(color.getRed(), color.getGreen(), color.getBlue(), 1.0f).next();
            bufferbuilder.vertex(x, y, 0.0D).color(color.getRed(), color.getGreen(), color.getBlue(), 1.0f).next();
        }
        tessellator.draw();
        clean3D();
    }

    public static Vec3d getInterpolationOffset(Entity e) {
        if (MinecraftClient.getInstance().isPaused()) return Vec3d.ZERO;
        double tickDelta = MinecraftClient.getInstance().getTickDelta();
        return new Vec3d(e.getX() - MathHelper.lerp(tickDelta, e.lastRenderX, e.getX()), e.getY() - MathHelper.lerp(tickDelta, e.lastRenderY, e.getY()), e.getZ() - MathHelper.lerp(tickDelta, e.lastRenderZ, e.getZ()));
    }

    public static Vec3d smoothen(Entity e) {
        return e.getPos().subtract(RenderUtils.getInterpolationOffset(e));
    }

    public static Box smoothen(Entity e, Box b) {
        return Box.of(RenderUtils.smoothen(e), b.getXLength(), b.getYLength(), b.getZLength()).offset(0, e.getHeight() / 2f, 0);
    }


    public static void setup() {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public static void setup3D() {
        setup();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
    }

    public static void clean() {
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }

    public static void clean3D() {
        clean();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
    }
}
