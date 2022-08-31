package me.vp.zyklon.util;


import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class RenderUtils {
    public static final MinecraftClient mc = MinecraftClient.getInstance();

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

    public static void drawLine3D(MatrixStack matrixStack, Vec3d start, Vec3d end, Color color) {
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

        bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        {
            bufferBuilder.vertex(matrix, startX, startY, startZ).next();
            bufferBuilder.vertex(matrix, endX, endY, endZ).next();
            bufferBuilder.vertex(matrix, startX, startY, startZ).next();
            bufferBuilder.vertex(matrix, endX, endY, endZ).next();
        }
        tessellator.draw();
        clean3D();
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
