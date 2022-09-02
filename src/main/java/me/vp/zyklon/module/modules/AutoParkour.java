package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.TickEvent;
import me.vp.zyklon.eventbus.Subscribe;
import me.vp.zyklon.module.Module;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import org.lwjgl.glfw.GLFW;

public class AutoParkour extends Module {

    public AutoParkour() {
        super("AutoParkour", "Automatically jumps at edges.", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (!mc.player.isSneaking() && mc.player.isOnGround()) {
            Box box = mc.player.getBoundingBox().offset(0, -0.51, 0);
            Iterable<VoxelShape> blockCollisions = mc.world.getBlockCollisions(mc.player, box);

            if (!blockCollisions.iterator().hasNext()) {
                if (!mc.player.isSprinting()) {
                    mc.player.setSprinting(true);
                    mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
                }
                mc.player.jump();
            }
        }
    }
}