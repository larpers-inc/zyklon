package me.vp.zyklon.setting.settings;

import me.vp.zyklon.Zyklon;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.Setting;


public class BooleanSetting extends Setting {
    public boolean enabled;

    public BooleanSetting(String name, Module parent, boolean enabled) {
        this.name = name;
        this.parent = parent;
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        Zyklon.INSTANCE.configManager.save();
    }

    public void toggle() {
        setEnabled(!enabled);
    }
}
