package dev.vili.zyklon.mixin;

import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.util.ZLogger;

import dev.vili.zyklon.util.ZNotification;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
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

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;setIcon(Ljava/io/InputStream;Ljava/io/InputStream;)V"))
    public void setAlternativeWindowIcon(net.minecraft.client.util.Window instance, InputStream icon16, InputStream icon32) throws IOException {
        window.setIcon(
                Zyklon.class.getResourceAsStream("/assets/zyklon/zyklon_32x32.png"),
                Zyklon.class.getResourceAsStream("/assets/zyklon/zyklon_64x64.png")
        );
    }

    @Inject(at = {@At(value = "HEAD")}, method = {"close()V"})
    private void onClose(CallbackInfo ci) {
        try {
            Zyklon.INSTANCE.configManager.save();
            ZLogger.logger.info("saved configs on exit.");
            ZLogger.logger.info("Good bye, " + Zyklon.mc.getSession().getUsername() + " !");
            ZNotification.sendNotification("Good bye, " + Zyklon.mc.getSession().getUsername(), TrayIcon.MessageType.INFO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "getFramerateLimit", at = @At("HEAD"), cancellable = true)
    private void onGetFramerateLimit(CallbackInfoReturnable<Integer> info) {
        if (Zyklon.INSTANCE.moduleManager.getModule("UnfocusedCPU").isEnabled()
                && !isWindowFocused()) info.setReturnValue(1);
    }
}