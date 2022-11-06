package dev.vili.zyklon.command.commands;

import dev.vili.zyklon.command.Command;
import net.minecraft.item.ItemStack;

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
            // If player is not operator, get items from inventory and drop them.
            for (int i = 0; i < 36; i++) {
                ItemStack itemStack = mc.player.getInventory().getStack(i);
                if (!itemStack.isEmpty()) {
                    mc.player.dropItem(itemStack, false);
                }
            }
        }

    }
}
