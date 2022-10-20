package me.vp.zyklon.module.modules;

import me.vp.zyklon.Zyklon;
import me.vp.zyklon.event.events.TickEvent;
import me.vp.zyklon.eventbus.Subscribe;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.BooleanSetting;
import me.vp.zyklon.util.ZLogger;
import net.minecraft.entity.player.PlayerEntity;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.awt.TrayIcon.MessageType;

public class VisualRange extends Module {
    public final BooleanSetting friends = new BooleanSetting("Friends", this, false);
    public final BooleanSetting trayMessage = new BooleanSetting("TrayMessage", this, true);

    private final ArrayList<PlayerEntity> spottedPlayers = new ArrayList<>();

    public VisualRange() {
        super("VisualRange", "Alerts you when a player enters your visual range.", GLFW.GLFW_KEY_UNKNOWN, Category.COMBAT);
        this.addSettings(friends, trayMessage);
    }

    @Override
    public void onDisable() {
        spottedPlayers.clear();
    }

    @Subscribe
    public void onTick(TickEvent event) {
        mc.world.getPlayers().forEach(player -> {
            if (!spottedPlayers.contains(player) && player != mc.player) {

                if (!friends.isEnabled() && Zyklon.INSTANCE.friendManager.isFriend(player.getName().getString())) return;

                spottedPlayers.add(player);

                ZLogger.warn(player.getName().getString() + " has entered your visual range!");
                if (trayMessage.isEnabled() && !mc.isWindowFocused()) {
                    ZLogger.trayMessage("Zyklon", player.getName().getString() + " has entered your visual range!", MessageType.INFO);
                }
            }
        });

        ArrayList<PlayerEntity> removedPlayers = new ArrayList<>();

        spottedPlayers.forEach(player -> {
            if (!mc.world.getPlayers().contains(player)) {
                removedPlayers.add(player);

                if (!friends.isEnabled() && Zyklon.INSTANCE.friendManager.isFriend(player.getName().getString())) return;

                ZLogger.warn(player.getName().getString() + " has left your visual range!");
                if (trayMessage.isEnabled() && !mc.isWindowFocused()) {
                    ZLogger.trayMessage("Zyklon", player.getName().getString() + " has left your visual range!", MessageType.INFO);
                }
            }
        });

        removedPlayers.forEach(spottedPlayers::remove);
    }

}
