package dev.vili.zyklon.clickgui.component.components.sub;

import dev.vili.zyklon.clickgui.component.Component;
import dev.vili.zyklon.clickgui.component.components.Button;
import dev.vili.zyklon.setting.settings.ModeSetting;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.Color;

public class ModeButton extends Component {
    private boolean hovered;
    private Button parent;
    private final ModeSetting setting;
    private int offset;
    private int x;
    private int y;
    public ModeButton(ModeSetting setting, Button button, int offset) {
        this.setting = setting;
        this.parent = button;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.offset = offset;
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
    }

    @Override
    public void renderComponent(MatrixStack matrixStack, TextRenderer textRenderer) {
        DrawableHelper.fill(matrixStack, parent.parent.getX() + 1, parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth()), parent.parent.getY() + offset + 12, this.hovered
                ? new Color(20, 20, 20, 191).getRGB() : new Color(0, 0, 0, 191).getRGB());

        DrawableHelper.fill(matrixStack, parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, new Color(0, 0, 0, 191).getRGB());
        DrawableHelper.drawStringWithShadow(matrixStack, textRenderer, this.setting.name + ": " + this.setting.getMode(), (parent.parent.getX() + 6), (parent.parent.getY() + offset) + 3, new Color(255, 255, 255, 255).getRGB());
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.hovered = isMouseOnButton(mouseX, mouseY);
        this.y = parent.parent.getY() + offset;
        this.x = parent.parent.getX();
        super.updateComponent(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY) && button == 0 && this.parent.open) {
            this.setting.cycle();

            this.setting.setMode(this.setting.getMode());
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12;
    }
}