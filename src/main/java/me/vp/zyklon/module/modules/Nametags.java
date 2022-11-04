package me.vp.zyklon.module.modules;

import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.BooleanSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class Nametags extends Module {
    public final BooleanSetting health = new BooleanSetting("Health", this, true);

    public Nametags() {
        super("Nametags", "Shows nametags through walls.", GLFW.GLFW_KEY_UNKNOWN, Category.RENDER);
        this.addSettings(health);
    }

    /* EntityRendererMixin */


    public Text addHealth(LivingEntity entity, Text nametag) {
        if (!health.isEnabled())
            return nametag;

        int health = (int) entity.getHealth();

        MutableText formattedHealth = Text.literal(" ").append(Integer.toString(health)).formatted(getColor(health));
        return ((MutableText)nametag).append(formattedHealth);
    }

    private Formatting getColor(int health) {
        if (health <= 5)
            return Formatting.DARK_RED;

        if (health <= 10)
            return Formatting.GOLD;

        if (health <= 15)
            return Formatting.YELLOW;

        return Formatting.GREEN;
    }
}
