package dev.vili.zyklon.command.commands;

import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.command.Command;
import dev.vili.zyklon.util.ZLogger;

import static dev.vili.zyklon.command.CommandManager.prefix;

public class FriendCmd extends Command {
    public FriendCmd() {
        super("friend", "add/remove friends.", "friend <add/remove/list>", "friends", "f");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 0) {
            ZLogger.error("Usage: " + prefix + syntax);
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            ZLogger.info("Friends: " + Zyklon.INSTANCE.friendManager.getFriends());
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (Zyklon.INSTANCE.friendManager.getFriends().contains(args[1])) {
                ZLogger.error(args[1] + " is already your friend!");
                return;
            } else {
                Zyklon.INSTANCE.friendManager.addFriend(args[1]);
                ZLogger.info("Added " + args[1] + " to your friends list!");
            }
        }
        if (args[0].equalsIgnoreCase("remove")) {
            if (!Zyklon.INSTANCE.friendManager.getFriends().contains(args[1])) {
                ZLogger.error(args[1] + " is not your friend!");
            } else {
                Zyklon.INSTANCE.friendManager.removeFriend(args[1]);
                ZLogger.info("Removed " + args[1] + " from your friends list!");
            }
        }
    }
}