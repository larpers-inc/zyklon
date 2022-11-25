package dev.vili.zyklon.command.commands;

import dev.vili.zyklon.command.Command;
import dev.vili.zyklon.util.ZLogger;
import net.minecraft.world.GameMode;

import static dev.vili.zyklon.command.CommandManager.prefix;

public class GmCmd extends Command {

    public GmCmd() {
        super("gm", "Changes your gamemode clientside.", "gm <mode>");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 0) {
            ZLogger.error("Usage: " + prefix + syntax);
        }
        for (GameMode gameMode : GameMode.values()) {
            if (gameMode.getName().equalsIgnoreCase(args[0])) {
                mc.interactionManager.setGameMode(gameMode);
                return;
            }
        }
    }
}
