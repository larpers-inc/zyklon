package dev.vili.zyklon.command.commands;

import dev.vili.zyklon.command.Command;
import dev.vili.zyklon.util.ZLogger;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.text.Text;

public class ReconnectCmd extends Command {

    public ReconnectCmd() {
        super("reconnect", "Reconnects to the server.", "reconnect");
    }
    int timer = 0;

    @Override
    public void onCommand(String[] args, String command) {
        if (mc.isInSingleplayer()) {
            ZLogger.error("You can't reconnect in singleplayer.");
            return;
        }
        String ip = mc.getCurrentServerEntry().address;
        mc.getNetworkHandler().getConnection().disconnect(Text.of("Reconnecting..."));
        while (timer < 10) {
            timer++;
        }
        mc.getNetworkHandler().sendPacket(new HandshakeC2SPacket(ip, 25565, NetworkState.STATUS));
    }
}
