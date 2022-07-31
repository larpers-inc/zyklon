package me.vp.zyklon.command.commands;

import me.vp.zyklon.Zyklon;
import me.vp.zyklon.command.Command;
import me.vp.zyklon.setting.FriendManager;
import me.vp.zyklon.util.ZLogger;

public class FriendCmd extends Command {
    public FriendCmd() {
        super("friend", "add/remove friends.", "friend <add/remove/list>", "friends");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 0) return;

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