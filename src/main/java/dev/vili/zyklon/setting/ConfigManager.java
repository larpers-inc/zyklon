package dev.vili.zyklon.setting;

import dev.vili.zyklon.clickgui.Clickgui;
import dev.vili.zyklon.clickgui.component.Frame;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.*;
import dev.vili.zyklon.util.ZLogger;
import dev.vili.zyklon.Zyklon;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.util.ArrayList;

public class ConfigManager {
    public File MainDirectory;

    public ConfigManager() {
        MainDirectory = new File(MinecraftClient.getInstance().runDirectory, Zyklon.name);
        if (!MainDirectory.exists()) {
            MainDirectory.mkdir();
        }
    }

    // ---------- Save ----------

    public void save() {
        saveModules();
        saveSettings();
        saveClickgui();
        savePrefix();
    }

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

    public void saveModules() {
        try {
            File file = new File(MainDirectory, "modules.txt");
            ArrayList<String> toSave = new ArrayList<>();

            for (Module module : Zyklon.INSTANCE.moduleManager.getModules()) {
                if (module.isEnabled() && !module.getName().equalsIgnoreCase("Clickgui") && !module.getName().equalsIgnoreCase("hudeditor") &&
                        !module.getName().equalsIgnoreCase("commandline") && !module.getName().equalsIgnoreCase("options")) {
                    toSave.add(module.getName());
                }
            }

            writeFile(toSave, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveSettings() {
        try {
            File file = new File(MainDirectory, "settings.txt");
            ArrayList<String> toSave = new ArrayList<>();

            Zyklon.INSTANCE.moduleManager.getModules().forEach(mod -> {
                for (Setting setting : mod.settings) {

                    if (setting instanceof BooleanSetting) {
                        toSave.add(mod.getName() + ":" + setting.name + ":" + ((BooleanSetting) setting).isEnabled());
                    }

                    if (setting instanceof NumberSetting) {
                        toSave.add(mod.getName() + ":" + setting.name + ":" + ((NumberSetting) setting).getValue());
                    }

                    if (setting instanceof ModeSetting) {
                        toSave.add(mod.getName() + ":" + setting.name + ":" + ((ModeSetting) setting).getMode());
                    }

                    if (setting instanceof ColorSetting) {
                        toSave.add(mod.getName() + ":" + setting.name + ":" + ((ColorSetting) setting).toInteger() + ":" + ((ColorSetting) setting).getRainbow());
                    }

                    if (setting instanceof KeybindSetting) {
                        toSave.add(mod.getName() + ":" + setting.name + ":" + mod.getKey());
                    }
                }
            });

            writeFile(toSave, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void savePrefix() {
        try {
            File file = new File(MainDirectory, "prefix.txt");
            ArrayList<String> toSave = new ArrayList<>();

            toSave.add(Zyklon.INSTANCE.commandManager.prefix);

            writeFile(toSave, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO
    public void saveClickgui() {
        try {
            File file = new File(MainDirectory, "clickgui.txt");
            ArrayList<String> toSave = new ArrayList<>();

            Clickgui.getFrames().forEach(frame -> {
                toSave.add(frame.category + ":" + frame.getX() + ":" + frame.getY() + ":" + frame.isOpen());
            });

            writeFile(toSave, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------- Load ----------
    public void load() {
        loadModules();
        loadSettings();
        loadClickgui();
        loadPrefix();
    }

    public void loadModules() {
        try {
            File file = new File(MainDirectory, "modules.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = br.readLine()) != null) {
                for (Module m : Zyklon.INSTANCE.moduleManager.getModules()) {
                    if (m.getName().equals(line)) {
                        m.toggle();
                        ZLogger.logger.info(m.getName() + " enabled.");
                    }
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadSettings() {
        try {
            File file = new File(MainDirectory, "settings.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line;

            while ((line = br.readLine()) != null) {
                String curLine = line.trim();
                String modname = curLine.split(":")[0];
                String settingname = curLine.split(":")[1];
                String value = curLine.split(":")[2];
                Module module = Zyklon.INSTANCE.moduleManager.getModule(modname);

                if (module != null) {
                    if (!settingname.equals("KeyBind")) {
                        Setting setting = Zyklon.INSTANCE.settingManager.getSettingByName(module, settingname);
                        if (setting instanceof BooleanSetting) {
                            ((BooleanSetting) setting).setEnabled(Boolean.parseBoolean(value));
                        }
                        if (setting instanceof NumberSetting) {
                            ((NumberSetting) setting).setValue(Double.parseDouble(value));
                        }
                        if (setting instanceof ModeSetting && ((ModeSetting) setting).modes.toString().contains(value)) {
                            ((ModeSetting) setting).setMode(value);
                        }
                        if (setting instanceof ColorSetting) {
                            ((ColorSetting) setting).setRainbow(Boolean.parseBoolean(curLine.split(":")[3]));
                            ((ColorSetting) setting).fromInteger(Integer.parseInt(value));
                        }
                        if (setting instanceof KeybindSetting) {
                            ((KeybindSetting) setting).setKeyCode(Integer.parseInt(value));
                        }
                    } else module.setKey(Integer.parseInt(value));
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO
    public void loadClickgui() {
        try {
            File file = new File(MainDirectory, "clickgui.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = br.readLine()) != null) {
                String curLine = line.trim();
                String x = curLine.split(":")[1];
                String y = curLine.split(":")[2];
                String open = curLine.split(":")[3];

                Frame frame = Clickgui.getFrameByCategory(curLine.strip().split(":")[0]);
                if (frame != null) {
                    frame.setX(Integer.parseInt(x));
                    frame.setY(Integer.parseInt(y));
                    frame.setOpen(Boolean.parseBoolean(open));
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPrefix() {
        try {
            File file = new File(MainDirectory, "prefix.txt");
            FileInputStream fstream = new FileInputStream(file.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = br.readLine()) != null) {
                Zyklon.INSTANCE.commandManager.setCommandPrefix(line);
            }

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}