package dev.vili.zyklon.command.commands;

import dev.vili.zyklon.command.Command;
import dev.vili.zyklon.util.ZLogger;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

import static dev.vili.zyklon.command.CommandManager.prefix;

public class TpPlusCmd extends Command {

    public TpPlusCmd() {
        super("tp+", "Teleports you to the specified coordinates.", "tp+ <x> <y> <z>");
    }


    @Override
    public void onCommand(String[] args, String command) {
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
            Vec3d tempPos = Math.ceil(startPos.distanceTo(endPos) / 8.5) > 1 ? startPos.add(endPos.subtract(startPos).normalize().multiply(8.5)) : endPos;
            for (int i = 0; i < Math.ceil(startPos.distanceTo(endPos) / 8.5); i++) {
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(tempPos.x, tempPos.y, tempPos.z, mc.player.isOnGround()));
                tempPos = tempPos.add(endPos.subtract(startPos).normalize().multiply(8.5));
                mc.player.updatePosition(tempPos.x, tempPos.y, tempPos.z);

                if (tempPos.distanceTo(endPos) <= 8.5) {
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(endPos.x, endPos.y, endPos.z, mc.player.isOnGround()));
                    mc.player.updatePosition(endPos.x, endPos.y, endPos.z);
                    break;
                }
            }

            /*
            for (int i = 0; i <= 7.5; i++) {
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        mc.player.getX() + step.x,
                        mc.player.getY() + step.y,
                        mc.player.getZ() + step.z,
                        false));
                mc.player.updatePosition(mc.player.getX() + step.x, mc.player.getY() + step.y, mc.player.getZ() + step.z);
            }

            // send final packet to prevent desync
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true));
            mc.player.updatePosition(x, y, z);

             */
            ZLogger.info("Tried teleporting to " + x + ", " + y + ", " + z + ".");
        } catch (NumberFormatException e) {
            ZLogger.error("Invalid coordinates.");
        }
    }

}