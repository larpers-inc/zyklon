package me.vp.zyklon.event.events;

import me.vp.zyklon.event.Event;
import net.minecraft.client.util.math.MatrixStack;

public class RenderIngameHudEvent extends Event {
    private MatrixStack matrixStack;

	public RenderIngameHudEvent(MatrixStack matrixStack) {
		this.matrixStack = matrixStack;
	}

	public MatrixStack getMatrix() {
		return matrixStack;
	}
}