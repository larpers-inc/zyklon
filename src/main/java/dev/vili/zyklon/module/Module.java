package dev.vili.zyklon.module;

import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.setting.Setting;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import dev.vili.zyklon.setting.settings.KeybindSetting;
import dev.vili.zyklon.util.ZLogger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public abstract class Module {
    public static MinecraftClient mc = MinecraftClient.getInstance();
    public String name, description;
    public Module parent;
    public KeybindSetting keyCode = new KeybindSetting(0);
    public Category category;
    public BooleanSetting hided = new BooleanSetting("Hided", this, false);
    public boolean enabled;
    public List<Setting> settings = new ArrayList<>();

    public Module(String name, String description, int key, Category category) {
        super();
        this.name = name;
        this.description = description;
        keyCode.code = key;
        addSettings(keyCode);
        addSettings(hided);
        this.category = category;
        enabled = false;
    }


    public enum Category {
        PLAYER("player"),
        RENDER("render"),
        COMBAT("combat"),
        MOVEMENT("movement"),
        MISC("misc"),
        CLIENT("client"),
        DUMMY("dummy");

        public final String name;
        Category(String name) {
            this.name = name;
        }
    }

    public void addSettings(Setting... settings) {
        this.settings.addAll(Arrays.asList(settings));
        this.settings.sort(Comparator.comparingInt(s -> s == keyCode ? 1 : 0));
    }

    public String getName() {
        return this.name;
    }

    public Category getCategory() {
        return this.category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getKey() {
        return keyCode.code;
    }

    public void setKey(int key) {
        this.keyCode.code = key;

        if (Zyklon.INSTANCE.configManager != null)
            Zyklon.INSTANCE.configManager.save();
    }

    public void setHided(boolean hided) {
        this.hided.enabled = hided;

        if (Zyklon.INSTANCE.configManager != null)
            Zyklon.INSTANCE.configManager.save();
    }

    public void toggle() {
        enabled = !enabled;
        if (enabled) {
            enable();
        } else disable();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (enabled)
            onEnable();
        else
            onDisable();
    }

    public void enable() {
        Zyklon.INSTANCE.EVENT_BUS.register(this);
        onEnable();
        setEnabled(true);
        if (Zyklon.INSTANCE.moduleManager.isModuleEnabled("ToggleInfo") && !this.getName().equalsIgnoreCase("Clickgui"))
            ZLogger.info(Formatting.GREEN + this.getName() + " enabled!");
    }

    public void disable() {
        Zyklon.INSTANCE.EVENT_BUS.unregister(this);
        onDisable();
        setEnabled(false);
        if (Zyklon.INSTANCE.moduleManager.isModuleEnabled("ToggleInfo") && !this.getName().equalsIgnoreCase("Clickgui"))
            ZLogger.info(Formatting.RED + this.getName() + " disabled!");
    }

    public void onTick() {
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

}
