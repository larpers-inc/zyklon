package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import dev.vili.zyklon.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class Nametags extends Module {
    public final BooleanSetting health = new BooleanSetting("Health", this, true);
    public final BooleanSetting ping = new BooleanSetting("Ping", this, true);
    public final BooleanSetting distance = new BooleanSetting("Distance", this, true);
    public final BooleanSetting gamemode = new BooleanSetting("Gamemode", this, false);

    public Nametags() {
        super("Nametags", "Better nametags.", GLFW.GLFW_KEY_UNKNOWN, Category.RENDER);
        this.addSettings(health, ping, distance, gamemode);
    }

    /* EntityRendererMixin */


    public Text addHealth(Entity entity, Text nametag) {
        if (!health.isEnabled())
            return nametag;

        if (entity instanceof LivingEntity) {
            int health = (int) ((LivingEntity) entity).getHealth();

            MutableText formattedHealth = Text.literal(" ").append(Integer.toString(health)).formatted(getColor(health));
            return ((MutableText) nametag).append(formattedHealth);
        }

        return null;
    }

    public Text addPing(Entity entity, Text nametag) {
        if (!ping.isEnabled())
            return nametag;

        if (entity instanceof PlayerEntity) {
            int ping = EntityUtils.getEntityPing((PlayerEntity) entity);

            MutableText formattedPing = Text.literal(" ").append(Integer.toString(ping)).formatted(getColor(ping));
            return ((MutableText) nametag).append(formattedPing);
        }

        return null;
    }

    public Text addDistance(Entity entity, Text nametag) {
        if (!distance.isEnabled())
            return nametag;

        double distance = mc.player.distanceTo(entity);
        String formattedDistance = String.format("%.1f", distance);
        MutableText formattedDistanceText = Text.literal(" ").append(formattedDistance + "m").formatted(Formatting.GRAY);

        return ((MutableText) nametag).append(formattedDistanceText);
    }

    public Text addGamemode(Entity entity, Text nametag) {
        if (!gamemode.isEnabled())
            return nametag;

        if (entity instanceof PlayerEntity) {
            String gamemode = String.valueOf(EntityUtils.getEntityGamemode((PlayerEntity) entity));
            MutableText formattedGamemode = Text.literal(" ").append(gamemode).formatted(Formatting.GRAY);

            return ((MutableText) nametag).append(formattedGamemode);
        }

        return null;
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
