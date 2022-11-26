package dev.vili.zyklon.mixin;

import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.event.events.OpenScreenEvent;
import dev.vili.zyklon.util.ZLogger;

import dev.vili.zyklon.util.ZNotification;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow public abstract boolean isWindowFocused();

    @Shadow @Final private Window window;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setOverlay(Lnet/minecraft/client/gui/screen/Overlay;)V", shift = At.Shift.BEFORE))
    public void init(RunArgs args, CallbackInfo callback) {
        Zyklon.INSTANCE.postInit();
    }


    @Inject(method = "getWindowTitle", at = @At("TAIL"), cancellable = true)
    public void getWindowTitle(CallbackInfoReturnable<String> cir) {
        if (Zyklon.INSTANCE.moduleManager.getModule("GameWindowTitle").isEnabled())
            cir.setReturnValue(Zyklon.name + " " + Zyklon.version + " - (" + cir.getReturnValue() + ")");
        else cir.setReturnValue(cir.getReturnValue());
    }

    @Inject(at = {@At(value = "HEAD")}, method = {"close()V"})
    private void onClose(CallbackInfo ci) {
        try {
            Zyklon.INSTANCE.configManager.save();
            ZLogger.logger.info("saved configs on exit.");
            ZLogger.logger.info("Good bye, " + Zyklon.mc.getSession().getUsername() + " !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "getFramerateLimit", at = @At("HEAD"), cancellable = true)
    private void onGetFramerateLimit(CallbackInfoReturnable<Integer> info) {
        if (Zyklon.INSTANCE.moduleManager.getModule("UnfocusedCPU").isEnabled()
                && !isWindowFocused()) info.setReturnValue(1);
    }

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void setScreen(Screen screen, CallbackInfo ci) {
        OpenScreenEvent event = new OpenScreenEvent(screen);
        Zyklon.INSTANCE.EVENT_BUS.post(event);

        if (event.isCancelled()) ci.cancel();
    }
}