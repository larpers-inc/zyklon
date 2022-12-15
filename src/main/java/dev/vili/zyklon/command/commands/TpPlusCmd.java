package dev.vili.zyklon.command.commands;

import dev.vili.zyklon.command.Command;
import dev.vili.zyklon.util.ZLogger;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static dev.vili.zyklon.command.CommandManager.prefix;

public class TpPlusCmd extends Command {

    public TpPlusCmd() {
        super("tp+", "Teleports you to the specified coordinates.", "tp+ <x> <y> <z>");
    }


    @Override
    public void onCommand(String[] args, String command) {
        if (!mc.player.isAlive()) return;
        if (args.length == 0) {
            ZLogger.error("Usage: " + prefix + syntax);
            return;
        }

        try {
            double x = Double.parseDouble(args[0]);
            double y = Double.parseDouble(args[1]);
            double z = Double.parseDouble(args[2]);

            // only move 8.5 blocks at a time to prevent kick
            Vec3d startPos = mc.player.getPos();
            Vec3d endPos = new Vec3d(x, y, z);
            Vec3d tempPos = Math.ceil(startPos.distanceTo(endPos) / 9.5) > 1 ? startPos.add(endPos.subtract(startPos).normalize().multiply(9.5)) : endPos;

            // Move to temp position first until we are close enough to the end position
            for (int i = 0; i < Math.ceil(startPos.distanceTo(endPos) / 9.5); i++) {
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(tempPos.x, tempPos.y, tempPos.z, false));
                tempPos = tempPos.add(endPos.subtract(startPos).normalize().multiply(9.5));

                if (i % 4 == 0)
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        ZLogger.warn("Interrupted while sleeping");
                    }

                // If we are close enough to the end position, move to the end position
                if (tempPos.distanceTo(endPos) < 9.5) {
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(endPos.x, endPos.y, endPos.z, false));
                    break;
                }
            }
            ZLogger.info("Tried teleporting to " + x + ", " + y + ", " + z + ".");
        } catch (NumberFormatException e) {
            ZLogger.error("Invalid coordinates.");
        }
    }
}