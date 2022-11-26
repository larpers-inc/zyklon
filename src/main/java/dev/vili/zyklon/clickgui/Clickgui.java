package dev.vili.zyklon.clickgui;

import dev.vili.zyklon.clickgui.component.Frame;
import dev.vili.zyklon.clickgui.effect.Snow;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.Zyklon;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Clickgui extends Screen {
    MinecraftClient mc = MinecraftClient.getInstance();
    private final Identifier waifu = new Identifier("zyklon", "zyklonwaifu.png");

    public static ArrayList<dev.vili.zyklon.clickgui.component.Frame> frames;
    private final ArrayList<Snow> snowList;

    public Clickgui() {
        super(Text.literal(Zyklon.name));
        frames = new ArrayList<>();
        snowList = new ArrayList<>();
        int frameX = 10;
        int frameY = 10;

        for (Module.Category category : Module.Category.values()) {
            dev.vili.zyklon.clickgui.component.Frame frame = new dev.vili.zyklon.clickgui.component.Frame(category);
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
        dev.vili.zyklon.module.modules.Clickgui clickgui = (dev.vili.zyklon.module.modules.Clickgui) Zyklon.INSTANCE.moduleManager.getModule("ClickGui");
        Random random = new Random();

        // Frames
        frames.forEach(frame -> {
            frame.renderFrame(matrixStack, textRenderer);
            frame.updatePosition(mouseX, mouseY);
            for (dev.vili.zyklon.clickgui.component.Component comp : frame.getComponents()) {
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

        for (int i = 0; i < 80; ++i) {
            for (int y = 0; y < 3; ++y) {
                Snow snow = new Snow(25 * i, y * -50, random.nextInt(3) + 1, random.nextInt(2) + 1);
                snowList.add(snow);
            }
        }

        // Draw vili.dev watermark at the bottom right
        DrawableHelper.fill(matrixStack, mc.getWindow().getScaledWidth() - 100, mc.getWindow().getScaledHeight() - 10, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight(), 0x80000000);
        textRenderer.drawWithShadow(matrixStack, "vili.dev", mc.getWindow().getScaledWidth() - 95, mc.getWindow().getScaledHeight() - 9, 0xFFFFFFFF);
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (dev.vili.zyklon.clickgui.component.Frame frame : frames) {
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
                    for (dev.vili.zyklon.clickgui.component.Component component : frame.getComponents()) {
                        component.mouseClicked((int) mouseX, (int) mouseY, button);
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (dev.vili.zyklon.clickgui.component.Frame frame : frames) {
            if (frame.isOpen() && keyCode != 1) {
                if (!frame.getComponents().isEmpty()) {
                    for (dev.vili.zyklon.clickgui.component.Component component : frame.getComponents()) {
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
        for (dev.vili.zyklon.clickgui.component.Frame frame : frames) {
            frame.setDrag(false);
        }
        for (dev.vili.zyklon.clickgui.component.Frame frame : frames) {
            if (frame.isOpen()) {
                if (!frame.getComponents().isEmpty()) {
                    for (dev.vili.zyklon.clickgui.component.Component component : frame.getComponents()) {
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

    public static List<Frame> getFrames() {
        return frames;
    }

    public static Frame getFrameByCategory(String cat) {
        return frames.stream().filter(frame -> frame.category.name().equalsIgnoreCase(cat)).findAny().orElse(null);
    }

}