package me.vp.zyklon.clickgui;

import me.vp.zyklon.Zyklon;
import me.vp.zyklon.module.Module.Category;
import me.vp.zyklon.clickgui.component.Frame;
import me.vp.zyklon.clickgui.component.Component;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;

public class Clickgui extends Screen {
    MinecraftClient mc = MinecraftClient.getInstance();
    private final Identifier logo = new Identifier("zyklon", "zyklonwaifu.png");

    public static ArrayList<Frame> frames;

    public Clickgui() {
        super(Text.literal(Zyklon.name));
        frames = new ArrayList<>();
        int frameX = 5;

        for (Category category : Category.values()) {
            Frame frame = new Frame(category);
            frame.setX(frameX);
            frames.add(frame);
            frameX += frame.getWidth() + 1;
        }

    }

    @Override
    public void init() {}

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        frames.forEach(frame -> {
            frame.renderFrame(matrixStack, textRenderer);
            frame.updatePosition(mouseX, mouseY);
            for (Component comp : frame.getComponents()) {
                comp.updateComponent(mouseX, mouseY);
            }
        });
        matrixStack.push();
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, logo);
        DrawableHelper.drawTexture(matrixStack, 580, mc.getWindow().getScaledHeight() - 140, 0, 0, 150, 150, 150, 150);
        matrixStack.pop();
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Frame frame : frames) {
            if (frame.isWithinHeader((int) mouseX, (int) mouseY) && button == 0) {
                frame.setDrag(true);
                frame.dragX = ((int) mouseX - frame.getX());
                frame.dragY = ((int) mouseY - frame.getY());
            }
            if (frame.isWithinHeader((int) mouseX, (int) mouseY) && button == 1) {
                frame.setOpen(!frame.isOpen());
            }
            if (frame.isOpen()) {
                if (!frame.getComponents().isEmpty()) {
                    for (Component component : frame.getComponents()) {
                        component.mouseClicked((int) mouseX, (int) mouseY, button);
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Frame frame : frames) {
            if (frame.isOpen() && keyCode != 1) {
                if (!frame.getComponents().isEmpty()) {
                    for (Component component : frame.getComponents()) {
                        component.keyTyped(keyCode, scanCode, modifiers);
                    }
                }
            }
        }

        if (keyCode == Zyklon.INSTANCE.moduleManager.getModule("Clickgui").keyCode.code) this.mc.setScreen(null);
        else if (keyCode == GLFW.GLFW_KEY_ESCAPE) this.mc.setScreen(null);

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Frame frame : frames) {
            frame.setDrag(false);
        }
        for (Frame frame : frames) {
            if (frame.isOpen()) {
                if (!frame.getComponents().isEmpty()) {
                    for (Component component : frame.getComponents()) {
                        component.mouseReleased((int) mouseX, (int) mouseY, button);
                    }
                }
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

}