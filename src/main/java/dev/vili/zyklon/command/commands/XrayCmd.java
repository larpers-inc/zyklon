package dev.vili.zyklon.command.commands;

import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.command.Command;
import dev.vili.zyklon.util.ZLogger;
import net.minecraft.client.MinecraftClient;

import java.awt.*;
import java.io.File;

public class XrayCmd extends Command {
    public XrayCmd() {
        super("xray", "Opens xray.txt file.", "xray", "");
    }

    private final File MainDirectory = new File(MinecraftClient.getInstance().runDirectory, Zyklon.name);
    private final File file = new File(MainDirectory, "xray.txt");

    @Override
    public void onCommand(String[] args, String command) {
        if (!Desktop.isDesktopSupported())
            ZLogger.error("Desktop is not supported.");
        else
            try {
                Desktop desktop = Desktop.getDesktop();
                if (file.exists())
                    desktop.open(file);
            } catch (Exception e) {
                e.printStackTrace();
                ZLogger.error("Could not open the file.");
            }
    }
}

