package dev.vili.zyklon.clickgui.component;

import dev.vili.zyklon.clickgui.component.components.Button;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.Zyklon;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.ArrayList;

public class Frame {

	public ArrayList<Component> components;
	public Module.Category category;
	private boolean open;
	private final int width;
	private int y;
	private int x;
	private final int barHeight;
	private boolean isDragging;
	public int dragX;
	public int dragY;

	public Frame(Module.Category cat) {
		this.components = new ArrayList<>();
		this.category = cat;
		this.width = 88;
        this.x = 5;
        this.y = 5;
		this.barHeight = 13;
		this.dragX = 0;
		this.open = false;
		this.isDragging = false;
		int tY = this.barHeight;

		for (Module mod : Zyklon.INSTANCE.moduleManager.getModules()) {
			if (!mod.getCategory().equals(cat)) continue;
			dev.vili.zyklon.clickgui.component.components.Button modButton = new Button(mod, this, tY, open);
			this.components.add(modButton);
			tY += 12;
		}

		refresh();
	}

	public ArrayList<Component> getComponents() {
		return components;
	}

	public void setX(int newX) {
		this.x = newX;
	}

	public void setY(int newY) {
		this.y = newY;
	}

	public void setDrag(boolean drag) {
		this.isDragging = drag;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public void renderFrame(MatrixStack matrixStack, TextRenderer textRenderer) {
		// Draw frame
		DrawableHelper.fill(matrixStack, this.x, this.y , this.x + this.width, this.y + this.barHeight, new Color(0, 0, 0, 191).getRGB());
		// Fill outline of frame with cyan
		DrawableHelper.fill(matrixStack, this.x, this.y, this.x + this.width, this.y + 1, new Color(0, 255, 255).getRGB());
		DrawableHelper.fill(matrixStack, this.x, this.y, this.x + 1, this.y + this.barHeight, new Color(0, 255, 255).getRGB());
		DrawableHelper.fill(matrixStack, this.x, this.y + this.barHeight, this.x + this.width, this.y + this.barHeight + 1, new Color(0, 255, 255).getRGB());
		DrawableHelper.fill(matrixStack, this.x + this.width, this.y, this.x + this.width + 1, this.y + this.barHeight, new Color(0, 255, 255).getRGB());
		// Draw text
		DrawableHelper.drawCenteredText(matrixStack, textRenderer, this.category.name(), (this.x + 40) + 3, (int) ((this.y + 0.0f) * 1 + 4), new Color(255, 255, 255, 255).getRGB());
		if (!this.components.isEmpty()) {
			DrawableHelper.drawStringWithShadow(matrixStack, textRenderer,
					this.open ? "-" : "+", (this.x + this.width - 10),
					(this.y) + 4, new Color(255, 255, 255, 255).getRGB());
		}
		if (this.open) {
			if (!this.components.isEmpty()) {
				for (Component component : components) {
					component.renderComponent(matrixStack, textRenderer);
				}
			}
		}
	}

	public void refresh() {
		int off = this.barHeight;
		for (Component comp : components) {
			comp.setOff(off);
			off += comp.getHeight();
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public void updatePosition(int mouseX, int mouseY) {
		if (this.isDragging) {
			this.setX(mouseX - dragX);
			this.setY(mouseY - dragY);
		}
	}

	public boolean isWithinHeader(int x, int y) {
		return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.barHeight;
	}

}