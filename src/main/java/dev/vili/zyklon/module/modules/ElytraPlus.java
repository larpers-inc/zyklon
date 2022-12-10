package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.ClientMoveEvent;
import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import dev.vili.zyklon.setting.settings.ModeSetting;
import dev.vili.zyklon.setting.settings.NumberSetting;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class ElytraPlus extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", this, "Control", "Control");
    public BooleanSetting autoOpen = new BooleanSetting("AutoOpen", this, true);
    public NumberSetting speed = new NumberSetting("Speed", this, 2.35, 0.01, 3, 0.01);

    public ElytraPlus() {
        super("Elytra+", "Better elytra.", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
        this.addSettings(mode, autoOpen, speed);
    }

    @Subscribe
    public void onClientMove(ClientMoveEvent event) {
        if (this.mode.is("Control") && mc.player.isFallFlying()) {
            if (!mc.options.jumpKey.isPressed() && !mc.options.sneakKey.isPressed()) {
                event.setVec(new Vec3d(event.getVec().x, -0.0001, event.getVec().z));
            }

            if (!mc.options.backKey.isPressed() && !mc.options.leftKey.isPressed()
                    && !mc.options.rightKey.isPressed() && !mc.options.forwardKey.isPressed()) {
                event.setVec(new Vec3d(0, event.getVec().y-0.0001, 0));
            }
        }
    }

    @Subscribe
    public void onTick(TickEvent event) {
        Vec3d vec;
        if (mc.world.getRegistryKey().getValue().getPath().equalsIgnoreCase("the_end"))
            vec = new Vec3d(0, 0, speed.getValue()).rotateX(mode.is("Control") ? 0
                    : -(float) Math.toRadians(mc.player.getPitch())).rotateY(-(float) Math.toRadians(mc.player.getYaw()));
        else if (mc.world.getRegistryKey().getValue().getPath().equalsIgnoreCase("the_nether"))
            vec = new Vec3d(0, 0, speed.getValue()).rotateX(mode.is("Control") ? 0
                    : -(float) Math.toRadians(mc.player.getPitch())).rotateY(-(float) Math.toRadians(mc.player.getYaw()));
        else
            vec = new Vec3d(0, 0, speed.getValue()).rotateX(mode.is("Control") ? 0
                    : -(float) Math.toRadians(mc.player.getPitch())).rotateY(-(float) Math.toRadians(mc.player.getYaw()));

        if (!mc.player.isFallFlying() && !mc.player.isOnGround() && mode.is("Control") && mc.player.age % 10 == 0 && autoOpen.isEnabled())
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));

        if (mc.player.isFallFlying()) {
            if (this.mode.is("Control")) {
                if (mc.options.backKey.isPressed()) vec = vec.multiply(-1);
                if (mc.options.leftKey.isPressed()) vec = vec.rotateY((float) Math.toRadians(90));
                if (mc.options.rightKey.isPressed()) vec = vec.rotateY(-(float) Math.toRadians(90));

                if (mc.options.jumpKey.isPressed()) vec = vec.add(0, speed.getValue(), 0);
                if (mc.options.sneakKey.isPressed()) vec = vec.add(0, -speed.getValue(), 0);

                if (!mc.options.backKey.isPressed() && !mc.options.leftKey.isPressed()
                        && !mc.options.rightKey.isPressed() && !mc.options.forwardKey.isPressed()
                        && !mc.options.jumpKey.isPressed() && !mc.options.sneakKey.isPressed()) vec = Vec3d.ZERO;
                mc.player.setVelocity(vec.multiply(2));
            }
        }
    }
}
