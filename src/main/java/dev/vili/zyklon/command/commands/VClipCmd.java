package dev.vili.zyklon.command.commands;

import dev.vili.zyklon.command.Command;
import dev.vili.zyklon.util.ZLogger;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

import static dev.vili.zyklon.command.CommandManager.prefix;

public class VClipCmd extends Command {

    public VClipCmd() {
        super("vclip", "Clip vertically.", "vclip <amount>");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 0) {
            ZLogger.error("Usage: " + prefix + syntax);
            return;
        }

        if (mc.player.hasVehicle()) {
            Entity vehicle = mc.player.getVehicle();
            vehicle.updatePosition(vehicle.getX(), vehicle.getY() + Double.parseDouble(args[0]), vehicle.getZ());
        }

        clip(Double.parseDouble(args[0]));
        mc.player.updatePosition(mc.player.getX(), mc.player.getY() + Double.parseDouble(args[0]), mc.player.getZ());
    }

    private void clip(double yPos) {
        if (!mc.player.isAlive()) return;

        Vec3d pos = mc.player.getPos();

        // loops to charge move packet
        for (int i = 0; i < 19; i++) {
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(pos.x, pos.y, pos.z, mc.player.getYaw(), mc.player.getPitch(), false));
        }

        // final move packet
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.x, yPos, pos.z, false));
    }
}
