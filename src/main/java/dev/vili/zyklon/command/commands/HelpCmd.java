package dev.vili.zyklon.command.commands;

import dev.vili.zyklon.command.Command;
import dev.vili.zyklon.command.CommandManager;
import dev.vili.zyklon.util.ZLogger;

import java.util.stream.Collectors;

public class HelpCmd extends Command {

    public HelpCmd() {
        super("help", "Tells you all the commands.", "help", "h");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 0) {
            ZLogger.info("Commands: " + CommandManager.commands.stream().map(Command::getName).collect(Collectors.joining(", ")));
        } else {
            for (Command cmd : CommandManager.commands) {
                if (cmd.getName().equalsIgnoreCase(args[0])) {
                    ZLogger.info(cmd.getSyntax());
                    return;
                }
            }
            ZLogger.error("Command not found.");
        }
	}
}
