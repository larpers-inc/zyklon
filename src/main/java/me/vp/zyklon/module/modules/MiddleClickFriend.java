package me.vp.zyklon.module.modules;

import me.vp.zyklon.Zyklon;
import me.vp.zyklon.event.events.KeyPressEvent;
import me.vp.zyklon.eventbus.Subscribe;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.util.ZLogger;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

public class MiddleClickFriend extends Module {

    public MiddleClickFriend() {
        super("MiddleClickFriend", "Adds a player to your friends list when you middle click them.", GLFW.GLFW_KEY_UNKNOWN, Category.MISC);
    }

    @Subscribe
    public void onKeyPress(KeyPressEvent event) {
        if (event.getKey() != GLFW.GLFW_MOUSE_BUTTON_MIDDLE) return;
        if (mc.crosshairTarget.getType() != HitResult.Type.ENTITY) return;

        EntityHitResult entityHitResult = (EntityHitResult) mc.crosshairTarget;
        if (!(entityHitResult.getEntity() instanceof PlayerEntity)) return;

        String name = entityHitResult.getEntity().getName().getString();

        if (Zyklon.INSTANCE.friendManager.isFriend(name)) {
            Zyklon.INSTANCE.friendManager.removeFriend(name);
            ZLogger.warn(name + " has been removed from your friends list.");
        } else {
            Zyklon.INSTANCE.friendManager.addFriend(name);
            ZLogger.info(name + " has been added to your friends list.");
        }
    }
}
