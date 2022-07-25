package me.vp.zyklon.mixin;

import me.vp.zyklon.Zyklon;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.event.events.KeyPressEvent;
import me.vp.zyklon.event.events.KeyReleaseEvent;


import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    MinecraftClient mc = MinecraftClient.getInstance();

    @Inject(method = "onKey", at = @At(value = "INVOKE", target = "net/minecraft/client/util/InputUtil.isKeyPressed(JI)Z", ordinal = 5))
    private void onKeyPress(long window, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (mc.currentScreen != null) return;
        if (action == 2) action = 1;
        switch (action) {
            case 0 -> {
                KeyReleaseEvent event = new KeyReleaseEvent(key, scanCode);
                Zyklon.INSTANCE.EVENT_BUS.post(event);
                if (event.isCancelled()) ci.cancel();
            }
            case 1 -> {
                KeyPressEvent event = new KeyPressEvent(key, scanCode);
                Zyklon.INSTANCE.moduleManager.getModules().stream().filter(m -> m.getKey() == key).forEach(Module::toggle);
                Zyklon.INSTANCE.EVENT_BUS.post(event);
                if (event.isCancelled()) ci.cancel();
            }
        }

    }
}