package dev.vili.zyklon.command.commands;

import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.command.Command;
import dev.vili.zyklon.util.ZLogger;
import net.minecraft.client.MinecraftClient;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static dev.vili.zyklon.command.CommandManager.prefix;

public class DeathsCmd extends Command {

    public DeathsCmd() {
        super("deaths", "Shows your deaths.", "deaths", "death");
    }

    private final File MainDirectory = new File(MinecraftClient.getInstance().runDirectory, Zyklon.name);
    private final File file = new File(MainDirectory, "deaths.txt");

    @Override
    public void onCommand(String[] args, String command) {
        try {
            if (file.exists()) {
                java.io.FileReader reader = new java.io.FileReader(file);
                java.io.BufferedReader bufferedReader = new java.io.BufferedReader(reader);
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    ZLogger.info(line);
                }
                reader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
