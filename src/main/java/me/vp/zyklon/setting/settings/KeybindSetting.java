package me.vp.zyklon.setting.settings;

import me.vp.zyklon.Zyklon;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.Setting;

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
