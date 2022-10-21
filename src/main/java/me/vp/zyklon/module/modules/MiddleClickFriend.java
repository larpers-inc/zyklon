package me.vp.zyklon.module.modules;

import me.vp.zyklon.Zyklon;
import me.vp.zyklon.event.events.KeyPressEvent;
import me.vp.zyklon.event.events.TickEvent;
import me.vp.zyklon.eventbus.Subscribe;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.util.ZLogger;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

public class MiddleClickFriend extends Module {
    private boolean buttonHeld = false;

    public MiddleClickFriend() {
        super("MiddleClickFriend", "Adds a player to your friends list when you middle click them.", GLFW.GLFW_KEY_UNKNOWN, Category.MISC);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS && !buttonHeld) {
            buttonHeld = true;

            Optional<Entity> lookingAt = DebugRenderer.getTargetedEntity(mc.player, 200);

            if (lookingAt.isPresent()) {
                Entity e = lookingAt.get();

                if (e instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity) e;

                    if (Zyklon.INSTANCE.friendManager.isFriend(player.getName().getString())) {
                        Zyklon.INSTANCE.friendManager.removeFriend(player.getName().getString());
                        ZLogger.info("Removed " + player.getName().getString() + " from your friends list.");
                    } else {
                        Zyklon.INSTANCE.friendManager.addFriend(player.getName().getString());
                        ZLogger.info("Added " + player.getName().getString() + " to your friends list.");
                    }
                }
            }
        } else if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_RELEASE) {
            buttonHeld = false;
        }
    }

}
