package me.vp.zyklon.setting.settings;

import me.vp.zyklon.Zyklon;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.Setting;

import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting {
    public int index;
    public List<String> modes;

    public ModeSetting(String name, Module parent, String defaultMode, String... modes) {
        this.name = name;
        this.parent = parent;
        this.modes = Arrays.asList(modes);
        this.index = this.modes.indexOf(defaultMode);
    }

    public String getMode() {
        return this.modes.get(this.index);
    }

    public void setMode(String mode) {
        this.index = this.modes.indexOf(mode);

        Zyklon.INSTANCE.configManager.save();
    }

    public boolean is(String mode) {
        return (this.index == this.modes.indexOf(mode));
    }

    public void cycle() {
        if (this.index < this.modes.size() - 1) {
            this.index++;
        } else {
            this.index = 0;
        }

        Zyklon.INSTANCE.configManager.save();
    }
}
