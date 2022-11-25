package dev.vili.zyklon.command.commands;

import dev.vili.zyklon.command.Command;
import dev.vili.zyklon.util.FakePlayerUtil;
import dev.vili.zyklon.util.ZLogger;

import static dev.vili.zyklon.command.CommandManager.prefix;

public class FakePlayerCmd extends Command {

    public FakePlayerCmd() {
        super("fakeplayer", "Spawns a fake player.", "fakeplayer <add/del>");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 0) {
            ZLogger.error("Usage: " + prefix + syntax);
        } else if (args[0].equalsIgnoreCase("add")) {
            FakePlayerUtil.spawn();
        } else if (args[0].equalsIgnoreCase("del")) {
            FakePlayerUtil.despawn();
        }
    }
}
