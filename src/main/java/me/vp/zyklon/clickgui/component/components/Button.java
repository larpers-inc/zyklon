package me.vp.zyklon.clickgui.component.components;

import me.vp.zyklon.Zyklon;
import me.vp.zyklon.clickgui.component.Component;
import me.vp.zyklon.clickgui.component.Frame;
import me.vp.zyklon.clickgui.component.components.sub.*;
import me.vp.zyklon.clickgui.component.components.sub.Checkbox;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.module.modules.Clickgui;
import me.vp.zyklon.module.modules.ToggleSound;
import me.vp.zyklon.setting.Setting;
import me.vp.zyklon.setting.settings.BooleanSetting;
import me.vp.zyklon.setting.settings.ModeSetting;
import me.vp.zyklon.setting.settings.NumberSetting;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;

public class Button extends Component {

    public Module mod;
    public Frame parent;
    public int offset;
    private boolean isHovered;
    public ArrayList<Component> subcomponents = new ArrayList<>();
    public boolean open;
    private int height;
    private int opY;

    public Button(Module mod, Frame parent, int offset, boolean open) {
        this.mod = mod;
        this.parent = parent;
        this.offset = offset;
        this.open = open;
        height = 12;
        opY = offset + 12;

        // I am losing braincells
        for (Setting setting : mod.settings) {
            if (mod.settings != null) {
                if (setting instanceof BooleanSetting)
                    subcomponents.add(new Checkbox((BooleanSetting) setting, this, opY));
                if (setting instanceof ModeSetting)
                    subcomponents.add(new ModeButton((ModeSetting) setting, this, opY));
                if (setting instanceof NumberSetting)
                    subcomponents.add(new Slider((NumberSetting) setting, this, opY));
            }
        }
        this.subcomponents.add(new Keybind(this, opY));
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
        int opY = offset + 12;
        for (Component comp : this.subcomponents) {
            comp.setOff(opY);
            opY += 12;
        }
    }

    @Override
    public void renderComponent(MatrixStack matrixStack, TextRenderer textRenderer) {
        DrawableHelper.fill(matrixStack, parent.getX(), this.parent.getY() + this.offset, parent.getX() + parent.getWidth(), this.parent.getY() + 12 + this.offset, this.isHovered
                ? (this.mod.isEnabled() ? new Color(0, 208, 255, 191).darker().getRGB()
                : new Color(15, 15, 15, 191).getRGB()) : (this.mod.isEnabled() ? new Color(0, 208, 255, 191).getRGB()
                : new Color(30, 30, 30, 191).getRGB()));
        DrawableHelper.drawTextWithShadow(matrixStack, textRenderer, Text.of(this.mod.getName()), (parent.getX() + 2) + 2,
                (parent.getY() + offset + 2) + 1, new Color(255, 255, 255).getRGB());

        if (this.subcomponents.size() > 1)
            DrawableHelper.drawStringWithShadow(matrixStack, textRenderer, this.open ? "-" : "+", (parent.getX() + parent.getWidth() - 10),
                    (parent.getY() + offset) + 4, new Color(255, 255, 255, 255).getRGB());

        Clickgui clickgui = (Clickgui) Zyklon.INSTANCE.moduleManager.getModule("ClickGui");
        if (this.isHovered && clickgui.showDesc.isEnabled()) {
            DrawableHelper.drawTextWithShadow(matrixStack, textRenderer,
                    Text.of(this.mod.getDescription()), (parent.getX() + 2) + 95, (parent.getY() + this.offset + 5), new Color(255, 255, 255).getRGB());
        }

        if (this.open) {
            if (!this.subcomponents.isEmpty()) {
                for (Component comp : this.subcomponents) {
                    comp.renderComponent(matrixStack, textRenderer);
                }
                DrawableHelper.fill(matrixStack, parent.getX() + 2, parent.getY() + this.offset + 12, parent.getX() + 3,
                        parent.getY() + this.offset + ((this.subcomponents.size() + 1) * 12), new Color(0, 208, 255, 191).getRGB());
            }
        }
    }


    @Override
    public int getHeight() {
        if (this.open) {
            return (12 * (this.subcomponents.size() + 1));
        }
        return 12;
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.isHovered = isMouseOnButton(mouseX, mouseY);

        if (!this.subcomponents.isEmpty()) {
            for (Component comp : this.subcomponents) {
                comp.updateComponent(mouseX, mouseY);
            }
        }
        super.updateComponent(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY) && button == 0) {
            this.mod.toggle();
        }
        if (isMouseOnButton(mouseX, mouseY) && button == 1) {
            this.open = !this.open;
            this.parent.refresh();
        }
        for (Component comp : this.subcomponents) {
            comp.mouseClicked(mouseX, mouseY, button);
        }
        // Play a click sound
        ToggleSound toggleSound = (ToggleSound) Zyklon.INSTANCE.moduleManager.getModule("ToggleSound");
        if (isMouseOnButton(mouseX, mouseY) && button == 0 && toggleSound.isEnabled()) {
            Zyklon.mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }

        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        for (Component comp : this.subcomponents) {
            comp.mouseReleased(mouseX, mouseY, button);
        }
        super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void keyTyped(int keyCode, int scanCode, int modifiers) {
        for (Component comp : this.subcomponents) {
            comp.keyTyped(keyCode, scanCode, modifiers);
        }
        super.keyTyped(keyCode, scanCode, modifiers);
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > parent.getX() && x < parent.getX() + parent.getWidth() && y > this.parent.getY() + this.offset && y < this.parent.getY() + 12 + this.offset;
    }

}