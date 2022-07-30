package me.vp.zyklon.mixin;

import io.netty.channel.ChannelHandlerContext;
import me.vp.zyklon.Zyklon;
import me.vp.zyklon.command.CommandManager;
import me.vp.zyklon.event.events.PacketEvent;
import net.minecraft.class_7648;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
	private void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        PacketEvent.Receive event = new PacketEvent.Receive(packet);
        Zyklon.INSTANCE.EVENT_BUS.post(event);
        if (event.isCancelled()) ci.cancel();
    }

	@Inject(method = "send(Lnet/minecraft/network/Packet;Lnet/minecraft/class_7648;)V", at = @At("HEAD"), cancellable = true)
	private void packetSend(Packet<?> packet, class_7648 packetCallback, CallbackInfo ci) {
        if (packet instanceof ChatMessageC2SPacket pack) {
            if (pack.chatMessage().startsWith(CommandManager.prefix)) {
				CommandManager.callCommandReturn(pack.chatMessage());
				ci.cancel();
			}
		}

        PacketEvent.Send event = new PacketEvent.Send(packet);
        Zyklon.INSTANCE.EVENT_BUS.post(event);
        if (event.isCancelled()) ci.cancel();
    }
}
