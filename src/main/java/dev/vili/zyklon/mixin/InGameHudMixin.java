package dev.vili.zyklon.mixin;

import dev.vili.zyklon.module.modules.Freecam;
import dev.vili.zyklon.module.modules.NoOverlay;
import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.event.events.RenderIngameHudEvent;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	@Shadow
	private void renderOverlay(Identifier texture, float opacity) {}
	@Shadow private void renderCrosshair(MatrixStack matrices) {}

	@Shadow
	@Final
	private static Identifier PUMPKIN_BLUR;

	@Shadow
	@Final
	private static Identifier POWDER_SNOW_OUTLINE;

    @Inject(method = "render", at = @At("RETURN"), cancellable = true)
	private void render(MatrixStack matrixStack, float tickDelta, CallbackInfo ci) {
		RenderIngameHudEvent event = new RenderIngameHudEvent(matrixStack);
		Zyklon.INSTANCE.EVENT_BUS.post(event);

		if (event.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true)
	public void renderOverlay(Identifier texture, float opacity, CallbackInfo ci) {
		NoOverlay noOverlay = (NoOverlay) Zyklon.INSTANCE.moduleManager.getModule("NoOverlay");

		if (noOverlay != null && noOverlay.isEnabled()) {
			if ((noOverlay.pumpkin.isEnabled() && texture == PUMPKIN_BLUR) || (noOverlay.powderedSnow.isEnabled() && texture == POWDER_SNOW_OUTLINE)) {
				ci.cancel();
			}
		}
	}

	@Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
	public void renderStatusEffectOverlay(MatrixStack matrices, CallbackInfo ci) {
		NoOverlay noOverlay = (NoOverlay) Zyklon.INSTANCE.moduleManager.getModule("NoOverlay");

		if (noOverlay != null && noOverlay.isEnabled() && noOverlay.status.isEnabled()) ci.cancel();
	}

	@Inject(method = "renderVignetteOverlay", at = @At("HEAD"), cancellable = true)
	public void renderVignetteOverlay(Entity entity, CallbackInfo ci) {
		NoOverlay noOverlay = (NoOverlay) Zyklon.INSTANCE.moduleManager.getModule("NoOverlay");

		if (noOverlay != null && noOverlay.isEnabled() && noOverlay.vignette.isEnabled()) ci.cancel();
	}

	@Inject(method = "getCameraPlayer", at = @At("HEAD"), cancellable = true)
	public void getCameraPlayer(CallbackInfoReturnable<PlayerEntity> cir) {
		Freecam freecam = (Freecam) Zyklon.INSTANCE.moduleManager.getModule("Freecam");

		if (freecam != null && freecam.isEnabled()) {
			cir.setReturnValue(Zyklon.mc.player);
		}
	}
}