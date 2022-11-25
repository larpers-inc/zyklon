package dev.vili.zyklon.module.modules;

import com.google.common.collect.Streams;
import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import dev.vili.zyklon.setting.settings.NumberSetting;
import dev.vili.zyklon.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TpAura extends Module {
    public final BooleanSetting players = new BooleanSetting("Players", this, true);
    public final BooleanSetting friends = new BooleanSetting("Friends", this, false);
    public final BooleanSetting hostiles = new BooleanSetting("Hostiles", this, false);
    public final BooleanSetting friendly = new BooleanSetting("Friendly", this, false);
    public final NumberSetting range = new NumberSetting("Range", this, 3.5, 1, 10, 0.1);
    public final BooleanSetting deathDisable = new BooleanSetting("DeathDisable", this, true);
    public final BooleanSetting raycast = new BooleanSetting("Raycast", this, true);

    public TpAura() {
        super("TpAura", "Teleports randomly around entities.", GLFW.GLFW_KEY_UNKNOWN, Category.COMBAT);
        this.addSettings(players, friends, hostiles, friendly, range, deathDisable, raycast);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;
        if (mc.player.getHealth() <= 0 && deathDisable.isEnabled()) this.disable();

        try {
            for (Entity entity : getTargets()) {
                if (entity != null) {
                    if (entity.isLiving() && entity.isAttackable()) {
                        mc.player.updatePosition(entity.getX() + Math.random() * 2 - 1, entity.getY(), entity.getZ() + Math.random() * 2 - 1);
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
