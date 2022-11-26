package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.util.ZLogger;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
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
                        ZLogger.info(Formatting.RED + "Removed " + player.getName().getString() + " from your friends list.");
                    } else {
                        Zyklon.INSTANCE.friendManager.addFriend(player.getName().getString());
                        ZLogger.info(Formatting.GREEN + "Added " + player.getName().getString() + " to your friends list.");
                    }
                }
            }
        } else if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_RELEASE) {
            buttonHeld = false;
        }
    }

}
