package me.vp.zyklon.clickgui;

import me.vp.zyklon.Zyklon;
import me.vp.zyklon.clickgui.effect.Snow;
import me.vp.zyklon.module.Module.Category;
import me.vp.zyklon.clickgui.component.Frame;
import me.vp.zyklon.clickgui.component.Component;

import me.vp.zyklon.util.RenderUtils;
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
import java.util.Random;

public class Clickgui extends Screen {
    MinecraftClient mc = MinecraftClient.getInstance();
    private final Identifier waifu = new Identifier("zyklon", "zyklonwaifu.png");

    public static ArrayList<Frame> frames;
    private final ArrayList<Snow> snowList;

    public Clickgui() {
        super(Text.literal(Zyklon.name));
        frames = new ArrayList<>();
        snowList = new ArrayList<>();
        int frameX = 10;
        int frameY = 10;

        for (Category category : Category.values()) {
            Frame frame = new Frame(category);
            frames.add(frame);
            frame.setX(frameX);
            frame.setY(frameY);
            frameX += frame.getWidth() + 10;
        }

    }

    @Override
    public void init() {}

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        me.vp.zyklon.module.modules.Clickgui clickgui = (me.vp.zyklon.module.modules.Clickgui) Zyklon.INSTANCE.moduleManager.getModule("ClickGui");
        Random random = new Random();

        // Frames
        frames.forEach(frame -> {
            frame.renderFrame(matrixStack, textRenderer);
            frame.updatePosition(mouseX, mouseY);
            for (Component comp : frame.getComponents()) {
                comp.updateComponent(mouseX, mouseY);
            }
        });
        
        // Anime Girladasdhashasdjhasdasdasdas
        if (clickgui.waifu.isEnabled()) {
            RenderSystem.enableBlend();
            RenderSystem.setShaderTexture(0, waifu);
            DrawableHelper.drawTexture(matrixStack, 580, mc.getWindow().getScaledHeight() - 140, 0, 0, 150, 150, 150, 150);
            RenderSystem.disableBlend();
            RenderSystem.disableTexture();
        }

        // Snow effect
        if (!snowList.isEmpty() && clickgui.snow.isEnabled()) {
            snowList.forEach(Snow::Update);

            if (!clickgui.snow.isEnabled()) snowList.clear();
            if (snowList.size() >= 120) {
                for (int i = 120; i < snowList.size(); i++) {
                    snowList.remove(i);
                }
            }
        }

        for (int i = 0; i < 100; ++i) {
            for (int y = 0; y < 3; ++y) {
                Snow snow = new Snow(25 * i, y * -50, random.nextInt(3) + 1, random.nextInt(2) + 1);
                snowList.add(snow);
            }
        }
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

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) this.mc.setScreen(null);

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

    public static ArrayList<Frame> getFrames() {
        return frames;
    }

    public static Frame getFrameByCategory(String cat) {
        return frames.stream().filter(frame -> frame.category.name().equalsIgnoreCase(cat)).findAny().orElse(null);
    }

}