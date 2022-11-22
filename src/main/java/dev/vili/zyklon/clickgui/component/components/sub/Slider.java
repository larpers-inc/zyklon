package dev.vili.zyklon.clickgui.component.components.sub;

import dev.vili.zyklon.clickgui.component.Component;
import dev.vili.zyklon.clickgui.component.components.Button;
import dev.vili.zyklon.setting.settings.NumberSetting;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.awt.Color;

public class Slider extends Component {
	private boolean hovered;
	private final NumberSetting setting;
	private Button parent;
	private int offset;
	private int x;
	private int y;
	private boolean dragging = false;

	private double renderWidth;

	public Slider(NumberSetting setting, Button button, int offset) {
		this.setting = setting;
		this.parent = button;
		this.x = button.parent.getX() + button.parent.getWidth();
		this.y = button.parent.getY() + button.offset;
		this.offset = offset;
	}

	@Override
	public void renderComponent(MatrixStack matrixStack, TextRenderer textRenderer) {
		DrawableHelper.fill(matrixStack, parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + parent.parent.getWidth(), parent.parent.getY() + offset + 12, this.hovered
        ? new Color(20, 20, 20, 191).getRGB() : new Color(0, 0, 0, 191).getRGB());

		DrawableHelper.fill(matrixStack, parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX()
				+ (int) renderWidth, parent.parent.getY() + offset + 12, new Color(62, 99, 105, 191).getRGB());
		DrawableHelper.fill(matrixStack, parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, new Color(0, 0, 0, 191).getRGB());
		DrawableHelper.drawStringWithShadow(matrixStack, textRenderer, Formatting.WHITE + this.setting.name + ": "+ Formatting.RESET + this.setting.getValue() , (parent.parent.getX() + 6),
                                            (parent.parent.getY() + offset) + 3, new Color(255, 255, 255, 255).getRGB());
	}

	@Override
	public void setOff(int newOff) {
		offset = newOff;
	}

	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.hovered = isMouseOnButtonD(mouseX, mouseY) || isMouseOnButtonI(mouseX, mouseY);
		this.y = parent.parent.getY() + offset;
		this.x = parent.parent.getX();

		double diff = Math.min(88, Math.max(0, mouseX - this.x));

		double min = setting.getMinimum();
		double max = setting.getMaximum();

		renderWidth = (88) * (setting.getValue() - min) / (max - min);

		if (dragging) {
			if (diff == 0) {
				setting.setValue(setting.getMinimum());
			}
			else {
				double newValue = roundToPlace(((diff / 88) * (max - min) + min));
				setting.setValue(newValue);
			}
		}
        super.updateComponent(mouseX, mouseY);
	}

	private static double roundToPlace(double value) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(2, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if (isMouseOnButtonD(mouseX, mouseY) && button == 0 && this.parent.open) {
			dragging = true;
		}
		if (isMouseOnButtonI(mouseX, mouseY) && button == 0 && this.parent.open) {
			dragging = true;
		}
        super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int button) {
		dragging = false;
        super.mouseReleased(mouseX, mouseY, button);
	}

	public boolean isMouseOnButtonD(int x, int y) {
		return x > this.x && x < this.x + (parent.parent.getWidth() / 2 + 1) && y > this.y && y < this.y + 12;
	}

	public boolean isMouseOnButtonI(int x, int y) {
		return x > this.x + parent.parent.getWidth() / 2 && x < this.x + parent.parent.getWidth() && y > this.y && y < this.y + 12;
	}
}