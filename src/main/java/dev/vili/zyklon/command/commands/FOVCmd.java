package dev.vili.zyklon.command.commands;

import dev.vili.zyklon.command.Command;
import dev.vili.zyklon.util.ZLogger;

public class FOVCmd extends Command {

    public FOVCmd() {
        super("fov", "Changes your FOV.", "fov <num>");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 0) {
            ZLogger.error("Please specify a number.");
            return;
        }
        try {
            int fov = Integer.parseInt(args[0]);
            mc.options.getFov().setValue(fov);
            ZLogger.info("FOV set to " + fov);
        } catch (NumberFormatException e) {
            ZLogger.error("Please specify a valid number.");
        }
    }
}
