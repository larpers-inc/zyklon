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
        ZLogger.info("Commands (" + CommandManager.commands.size() + "): " + CommandManager.commands.stream().map(Command::getName).collect(Collectors.joining(", ")));
	}
}
