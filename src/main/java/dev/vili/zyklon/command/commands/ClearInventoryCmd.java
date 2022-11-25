package dev.vili.zyklon.command.commands;

import dev.vili.zyklon.command.Command;
import dev.vili.zyklon.util.InventoryUtils;
import dev.vili.zyklon.util.ZLogger;

import static dev.vili.zyklon.command.CommandManager.prefix;

public class ClearInventoryCmd extends Command {

    public ClearInventoryCmd() {
        super("clearinventory", "Clears your inventory.", "clearinventory", "ci");
    }

    @Override
    public void onCommand(String[] args, String command) {
        // If player is operator, clear inventory.
        if (mc.player.isCreativeLevelTwoOp()) {
            mc.player.getInventory().clear();
        } else {
            InventoryUtils.dropInventory();
        }

    }
}
