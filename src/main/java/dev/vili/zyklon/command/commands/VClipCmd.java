package dev.vili.zyklon.command.commands;

import dev.vili.zyklon.command.Command;
import dev.vili.zyklon.util.ZLogger;
import net.minecraft.entity.Entity;

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
            vehicle.setPosition(vehicle.getX(), vehicle.getY() + Integer.parseInt(args[0]), vehicle.getZ());
        }

        mc.player.setPosition(mc.player.getX(), mc.player.getY() + Integer.parseInt(args[0]), mc.player.getZ());
    }
}
