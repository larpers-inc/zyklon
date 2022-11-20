package dev.vili.zyklon.command.commands;

import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.command.Command;
import dev.vili.zyklon.util.ZLogger;

public class ToggleCmd extends Command {

    public ToggleCmd() {
        super("toggle", "Toggles a module", "toggle <module>", "t");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 0) {
            ZLogger.error("Please specify a module.");
            return;
        }

        String moduleName = args[0];
        Module module = Zyklon.INSTANCE.moduleManager.getModule(String.valueOf(moduleName));

        if (module == null) {
            ZLogger.error("Module not found.");
            return;
        }

        module.toggle();
        ZLogger.info("Toggled " + module.getName() + ".");
    }
}
