package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import dev.vili.zyklon.setting.settings.ModeSetting;
import dev.vili.zyklon.setting.settings.NumberSetting;
import dev.vili.zyklon.util.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* Credits to @BleachDev */
public class ArrowDodge extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", this, "Client", "Client", "Packet");
    public final NumberSetting speed = new NumberSetting("Speed", this, 0.5, 0.1, 2, 0.1);
    public final NumberSetting predict = new NumberSetting("Predict", this, 250, 1, 500, 1);
    public final BooleanSetting upMovement = new BooleanSetting("UpMovement", this, false);

    public ArrowDodge() {
        super("ArrowDodge", "Dodges arrows.", GLFW.GLFW_KEY_UNKNOWN, Category.COMBAT);
        this.addSettings(mode, speed, predict, upMovement);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        for (Entity e : mc.world.getEntities()) {
            if (e.age > 75 || !(e instanceof ArrowEntity) || ((ArrowEntity) e).getOwner() == mc.player)
                continue;

            int steps = (int) predict.getValue();

            Box playerBox = mc.player.getBoundingBox().expand(0.3);
            List<Box> futureBoxes = new ArrayList<>(steps);

            Box currentBox = e.getBoundingBox();
            Vec3d currentVel = e.getVelocity();

            for (int i = 0; i < steps; i++) {
                currentBox = currentBox.offset(currentVel);
                currentVel = currentVel.multiply(0.99, 0.94, 0.99);
                futureBoxes.add(currentBox);

                if (!mc.world.getOtherEntities(null, currentBox).isEmpty() || WorldUtils.doesBoxCollide(currentBox)) {
                    break;
                }
            }

            for (Box box: futureBoxes) {
                if (playerBox.intersects(box)) {
                    for (Vec3d vel : getMoveVecs(e.getVelocity())) {
                        Box newBox = mc.player.getBoundingBox().offset(vel);

                        if (!WorldUtils.doesBoxCollide(newBox) && futureBoxes.stream().noneMatch(playerBox.offset(vel)::intersects)) {
                            if (mode.is("Client") && vel.y == 0) {
                                mc.player.setVelocity(vel);
                            } else if (mode.is("Packet")) {
                                mc.player.updatePosition(mc.player.getX() + vel.x, mc.player.getY() + vel.y, mc.player.getZ() + vel.z);
                                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), false));
                            }

                            return;
                        }
                    }
                }
            }
        }
    }

    private List<Vec3d> getMoveVecs(Vec3d arrowVec) {
        List<Vec3d> list = new ArrayList<>(Arrays.asList(
                arrowVec.subtract(0, arrowVec.y, 0).normalize().multiply(speed.getValue()).rotateY((float) -Math.toRadians(90f)),
                arrowVec.subtract(0, arrowVec.y, 0).normalize().multiply(speed.getValue()).rotateY((float) Math.toRadians(90f))));

        Collections.shuffle(list);

        if (upMovement.isEnabled()) {
            list.add(new Vec3d(0, 2, 0));
        }

        return list;
    }
}
