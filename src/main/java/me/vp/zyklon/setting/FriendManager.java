package me.vp.zyklon.setting;

import me.vp.zyklon.Zyklon;
import me.vp.zyklon.util.ZLogger;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.util.ArrayList;

public class FriendManager {
    public File MainDirectory;

    public FriendManager() {
        MainDirectory = new File(MinecraftClient.getInstance().runDirectory, Zyklon.name);
    }
    private final ArrayList<String> friends = new ArrayList<>();

    private void writeFile(ArrayList<String> toSave, File file) {
        try {
            PrintWriter printWriter = new PrintWriter(file);
            for (String string : toSave) {
                printWriter.println(string);
            }
            printWriter.close();
        } catch (FileNotFoundException ignored) {
        }
    }

    public void load() {
        try {
            File file = new File(MainDirectory, "friends.txt");
            ArrayList<String> toSave = new ArrayList<>();
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = br.readLine()) != null) {
                toSave.add(line);
                friends.add(line);
                ZLogger.logger.info("Added friend: " + line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            File file = new File(MainDirectory, "friends.txt");
            ArrayList<String> toSave = new ArrayList<>();

            friends.forEach(friend -> {
                toSave.add(friend);
            });

            writeFile(toSave, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void addFriend(String name) {
        friends.add(name);
        save();
    }

    public void removeFriend(String name) {
        friends.remove(name);
        save();
    }

    public String getFriends() {
        StringBuilder sb = new StringBuilder();
        friends.forEach(friend -> sb.append(friend).append(", "));
        return sb.toString();
    }
}