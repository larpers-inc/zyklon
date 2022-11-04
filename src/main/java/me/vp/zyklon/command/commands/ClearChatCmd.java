package me.vp.zyklon.command.commands;

import me.vp.zyklon.command.Command;

public class ClearChatCmd extends Command {

    public ClearChatCmd() {
        super("clearchat", "Clears the chat.", "clearchat", "cc");
    }

    @Override
    public void onCommand(String[] args, String command) {
        mc.inGameHud.getChatHud().clear(true);
    }
}
