package dev.vili.zyklon.command.commands;

import dev.vili.zyklon.command.Command;
import dev.vili.zyklon.util.ZLogger;

import static dev.vili.zyklon.command.CommandManager.prefix;

public class TpCmd extends Command {

    public TpCmd() {
        super("tp", "Teleports you to a player.", "tp <x> <y> <z>", "teleport");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 3) {
            try {
                double x = Double.parseDouble(args[0]);
                double y = Double.parseDouble(args[1]);
                double z = Double.parseDouble(args[2]);
                mc.player.teleport(x, y, z);
            } catch (NumberFormatException e) {
                ZLogger.error("Invalid coordinates.");
            }
        } else {
            ZLogger.error("Usage: " + prefix + syntax);
        }
    }
}
