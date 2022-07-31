package me.vp.zyklon.command.commands;

import me.vp.zyklon.command.Command;
import me.vp.zyklon.util.Friends;
import me.vp.zyklon.util.ZLogger;

public class FriendCmd extends Command {
    public FriendCmd() {
        super("friend", "add/remove friends.", "friend <add/remove/list>", "friends");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args[0] == null) return;

        if (args[0].equalsIgnoreCase("list")) {
            ZLogger.info("Friends: " + Friends.getInstance().friends);
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (Friends.getInstance().isFriend(args[1])) {
                ZLogger.error(args[1] + " is already your friend!");
                return;
            } else {
                Friends.getInstance().addFriend(args[1]);
                ZLogger.info("Added " + args[1] + " to your friends list!");
            }
        }
        if (args[0].equalsIgnoreCase("remove")) {
            if (!Friends.getInstance().isFriend(args[1])) {
                ZLogger.error(args[1] + " is not your friend!");
                return;
            } else {
                Friends.getInstance().removeFriend(args[1]);
                ZLogger.info("Removed " + args[1] + " from your friends list!");
            }
        }
    }
}