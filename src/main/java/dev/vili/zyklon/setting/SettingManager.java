package dev.vili.zyklon.setting;

import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.util.ZLogger;

import java.util.ArrayList;

public class SettingManager {

    private ArrayList<Setting> settings;

    public SettingManager() {
        this.settings = new ArrayList<>();
    }

    public void rSetting(Setting in) {
        this.settings.add(in);
    }

    public ArrayList<Setting> getSettings() {
        return this.settings;
    }

    public ArrayList<Setting> getSettingsByMod(Module mod) {
        ArrayList<Setting> out = new ArrayList<>();
        for (Setting s : getSettings()) {
            if (s.parent.equals(mod)) {
                out.add(s);
            }
        }
        if (out.isEmpty()) {
            return null;
        }
        return out;
    }

    public Setting getSettingByName(Module mod, String name) {
        for (Module m : Zyklon.INSTANCE.moduleManager.modules) {
            for (Setting set : m.settings) {
                if (set.name.equalsIgnoreCase(name) && set.parent == mod) {
                    return set;
                }
            }
        }
        ZLogger.logger.error("Setting not found: '" + name + "'!");
        return null;
    }
}
