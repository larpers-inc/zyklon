package me.vp.zyklon.mixin;

import me.vp.zyklon.Zyklon;
import me.vp.zyklon.event.events.PlaySoundEvent;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public class SoundManagerMixin {

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    public void playNoDelay(SoundInstance sound, CallbackInfo ci) {
        PlaySoundEvent playSoundEvent = new PlaySoundEvent(sound);

        Zyklon.INSTANCE.EVENT_BUS.post(playSoundEvent);
        if (playSoundEvent.isCancelled()) ci.cancel();
    }

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;I)V", at = @At("HEAD"), cancellable = true)
    public void playWithDelay(SoundInstance sound, int delay, CallbackInfo ci) {
        PlaySoundEvent playSoundEvent = new PlaySoundEvent(sound);

        Zyklon.INSTANCE.EVENT_BUS.post(playSoundEvent);
        if (playSoundEvent.isCancelled()) ci.cancel();
    }
}
