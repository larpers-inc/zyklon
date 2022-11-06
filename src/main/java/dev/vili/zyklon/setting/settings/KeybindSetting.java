package dev.vili.zyklon.setting.settings;

import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.setting.Setting;

public class KeybindSetting extends Setting {

    public int code;

    public KeybindSetting(int code) {
        this.name = "KeyBind";
        this.code = code;
    }

    public KeybindSetting(Module module) {
        // TODO Auto-generated constructor stub
    }

    public int getKeyCode() {
        return this.code;
    }

    public void setKeyCode(int code) {
        this.code = code;

        Zyklon.INSTANCE.configManager.save();
    }

}
