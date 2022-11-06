package dev.vili.zyklon.setting.settings;

import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.setting.Setting;


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
