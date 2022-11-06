package dev.vili.zyklon.mixin;

import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.module.modules.BetterChat;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.text.SimpleDateFormat;
import java.util.Date;

@Mixin(ChatHud.class)
public class ChatHudMixin extends DrawableHelper {
    private static final Date DATE = new Date();

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V",
            at = @At("HEAD"), argsOnly = true)
    private Text addMessageTimestamp(Text componentIn) {
        BetterChat betterChat = (BetterChat) Zyklon.INSTANCE.moduleManager.getModule("BetterChat");
        if (betterChat.isEnabled()) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            DATE.setTime(System.currentTimeMillis());

            MutableText newComponent = Text.literal("["+sdf.format(DATE) + "] ");

            if (betterChat.timestamps.isEnabled())
                newComponent.append(componentIn);

            return newComponent;
        }
        return componentIn;
    }
}
