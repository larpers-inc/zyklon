package dev.vili.zyklon.setting.settings;

import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.util.JColor;
import dev.vili.zyklon.setting.Setting;

public class ColorSetting extends Setting {

    private boolean rainbow;
    private JColor value;

    public ColorSetting(String name, Module parent, final JColor value) {
        this.name = name;
        this.parent = parent;
        this.value = value;
    }

    public JColor getValue() {
        if (rainbow) {
            return getRainbow(0, this.getColor().getAlpha());
        }
        return this.value;
    }

    public static JColor getRainbow(int incr, int alpha) {
        JColor color = JColor.fromHSB(((System.currentTimeMillis() + incr * 200) % (360 * 20)) / (360f * 20), 0.5f, 1f);
        return new JColor(color.getRed(), color.getBlue(), color.getGreen(), alpha);
    }

    public boolean getRainbow() {
        return this.rainbow;
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    public void setValue(boolean rainbow, final JColor value) {
        this.rainbow = rainbow;
        this.value = value;

        Zyklon.INSTANCE.configManager.save();
    }

    public long toInteger() {
        return this.value.getRGB();
    }

    public void fromInteger(long number) {
        this.value = new JColor(Math.toIntExact(number), true);
    }

    public JColor getColor() {
        return this.value;
    }
}
