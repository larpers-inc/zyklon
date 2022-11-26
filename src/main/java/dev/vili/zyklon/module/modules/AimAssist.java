package dev.vili.zyklon.module.modules;

import com.google.common.collect.Streams;
import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import dev.vili.zyklon.setting.settings.NumberSetting;
import dev.vili.zyklon.util.EntityUtils;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AimAssist extends Module {
    public final BooleanSetting players = new BooleanSetting("Players", this, true);
    public final BooleanSetting friends = new BooleanSetting("Friends", this, false);
    public final BooleanSetting hostiles = new BooleanSetting("Hostiles", this, false);
    public final BooleanSetting friendly = new BooleanSetting("Friendly", this, false);
    public final NumberSetting range = new NumberSetting("Range", this, 5, 1, 30, 1);
    public final BooleanSetting raycast = new BooleanSetting("Raycast", this, true);

    public AimAssist() {
        super("AimAssist", "Automatically aims at entities.", GLFW.GLFW_KEY_UNKNOWN, Category.COMBAT);
        this.addSettings(players, friends, hostiles, friendly, range, raycast);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;

        try {
            for (Entity entity : getTargets()) {
                if (entity != null) {
                    if (entity.isLiving() && entity.isAttackable()) {
                        Vec3d pos = entity.getEyePos();
                        EntityAnchorArgumentType.EntityAnchor anchor = EntityAnchorArgumentType.EntityAnchor.EYES;

                        mc.player.lookAt(anchor, pos);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private List<Entity> getTargets() {
        Stream<Entity> targets;
        targets = Streams.stream(mc.world.getEntities());
        Comparator<Entity> comparator;
        comparator = Comparator.comparing(mc.player::distanceTo);

        return targets
                .filter(e -> EntityUtils.isAttackable(e, true)
                        && mc.player.distanceTo(e) <= range.getValue())
                .filter(e -> (EntityUtils.isOtherServerPlayer(e) && players.isEnabled())
                        || (EntityUtils.isFriend(e) && friends.isEnabled())
                        || (EntityUtils.isMob(e) && hostiles.isEnabled())
                        || (EntityUtils.isAnimal(e) && friendly.isEnabled()))
                .filter(e -> !raycast.isEnabled() || mc.player.canSee(e))
                .sorted(comparator)
                .limit(1L)
                .collect(Collectors.toList());
    }
}
