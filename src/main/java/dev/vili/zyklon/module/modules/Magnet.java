package dev.vili.zyklon.module.modules;

import com.google.common.collect.Streams;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.setting.settings.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Magnet extends Module {
    public final NumberSetting range = new NumberSetting("Range",this, 5.5, 0.1, 8.5, 0.1);

    public Magnet() {
        super("Magnet", "Automatically picks up items.", GLFW.GLFW_KEY_UNKNOWN, Category.MISC);
        this.addSettings(range);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;

        for (Entity entity : getItems()) {
            if (entity instanceof ItemEntity) {
                double x = entity.getPos().getX();
                double y = entity.getPos().getY();
                double z = entity.getPos().getZ();
                //ZLogger.info("Magnet: " + x + " " + y + " " + z);

                // Check if entity has a block above it
                if (mc.world.getBlockState(entity.getBlockPos().up()).isAir()) {
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, mc.player.isOnGround()));
                }
            }
        }
    }

    private List<Entity> getItems() {
        Stream<Entity> items;
        items = Streams.stream(mc.world.getEntities());
        Comparator<Entity> comparator;
        comparator = Comparator.comparing(mc.player::distanceTo);

        return items
                .filter(entity -> entity instanceof ItemEntity
                        && mc.player.distanceTo(entity) <= range.getValue()
                        && mc.player.canSee(entity)
                        && !entity.isInsideWall()
                        && !entity.isInLava())
                .sorted(comparator)
                .limit(1)
                .collect(Collectors.toList());
    }

}
