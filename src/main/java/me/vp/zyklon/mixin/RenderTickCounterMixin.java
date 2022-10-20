package me.vp.zyklon.mixin;

import me.vp.zyklon.Zyklon;
import me.vp.zyklon.module.modules.Timer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderTickCounter.class)
public class RenderTickCounterMixin {
    @Shadow
    private float lastFrameDuration;
    @Shadow private float tickDelta;
    @Shadow private long prevTimeMillis;
    @Shadow private float tickTime;

    @Inject(method = "beginRenderTick", at = @At("HEAD"), cancellable = true)
    private void beginRenderTick(long timeMillis, CallbackInfoReturnable<Integer> cir) {
        Timer timer = (Timer) Zyklon.INSTANCE.moduleManager.getModule("Timer");
        if (timer.isEnabled()) {
            this.lastFrameDuration = (float) (((timeMillis - this.prevTimeMillis) / this.tickTime) * timer.speed.getValue());
            this.prevTimeMillis = timeMillis;
            this.tickDelta += this.lastFrameDuration;
            int i = (int) this.tickDelta;
            this.tickDelta -= i;

            cir.setReturnValue(i);
        }
    }
}
