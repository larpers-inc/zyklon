package dev.vili.zyklon.mixin;

import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.module.modules.NoOverlay;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack matrices, CallbackInfo ci) {
        NoOverlay noOverlay = (NoOverlay) Zyklon.INSTANCE.moduleManager.getModule("NoOverlay");

        if (noOverlay != null && noOverlay.isEnabled() && noOverlay.bossBar.isEnabled()) ci.cancel();
    }
}
