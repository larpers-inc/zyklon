package me.vp.zyklon.module.modules;

import com.google.common.collect.Streams;
import me.vp.zyklon.event.events.TickEvent;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.BooleanSetting;
import me.vp.zyklon.setting.settings.NumberSetting;
import me.vp.zyklon.util.EntityUtils;
import me.vp.zyklon.util.ZLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import me.vp.zyklon.eventbus.Subscribe;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Aura extends Module {
    public final BooleanSetting players = new BooleanSetting("Players", this, true);
    public final BooleanSetting friends = new BooleanSetting("Friends", this, false);
    public final BooleanSetting hostiles = new BooleanSetting("Hostiles", this, false);
    public final BooleanSetting friendly = new BooleanSetting("Friendly", this, false);
    public final BooleanSetting projectiles = new BooleanSetting("Projectiles", this, false);
    public final BooleanSetting raycast = new BooleanSetting("Raycast", this, true);
    public final BooleanSetting multiAura = new BooleanSetting("MultiAura", this, false);
    public final BooleanSetting itemSwitch = new BooleanSetting("ItemSwitch", this, true);
    public final NumberSetting range = new NumberSetting("Range", this, 3.5, 1, 10, 0.1);
    public final BooleanSetting delay = new BooleanSetting("1.9 Delay", this, true);
    public final BooleanSetting deathDisable = new BooleanSetting("DeathDisable", this, true);

    public Aura() {
        super("Aura", "Automatically attacks nearby entities.", GLFW.GLFW_KEY_UNKNOWN, Category.COMBAT);
        this.addSettings(players, hostiles, friendly, projectiles, raycast, multiAura, itemSwitch, range, delay, deathDisable);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;
        if (mc.player.getAttackCooldownProgress(0) < 1 && delay.isEnabled()) return;
        if (mc.player.getHealth() <= 0 && deathDisable.isEnabled()) this.disable();

        try {
            for (Entity entity : getTargets()) {
                if (entity != null) {
                    if (entity.isLiving() && entity.isAttackable() && !(entity.getClass() == EndCrystalEntity.class)) {
                        if (itemSwitch.isEnabled()) {
                            for (int i = 0; i < 9; i++) {
                                if (mc.player.getInventory().getStack(i).getItem() instanceof SwordItem)
                                    mc.player.getInventory().selectedSlot = i;
                            }
                        }
                        mc.interactionManager.attackEntity(mc.player, entity);
                        mc.player.swingHand(Hand.MAIN_HAND);

                        if (!multiAura.isEnabled()) break;
                    }
                }
            }
        } catch (Exception e) {
            ZLogger.logger.warn(e.getMessage());
        }
    }


    private List<Entity> getTargets() {
        Stream<Entity> targets;
        targets = Streams.stream(mc.world.getEntities());
        Comparator<Entity> comparator;
        comparator = Comparator.comparing(mc.player::distanceTo);

        return targets
                .filter(e -> EntityUtils.isAttackable(e, true)
                        && mc.player.distanceTo(e) <= range.getValue()
                        && (mc.player.canSee(e) || !raycast.isEnabled()))
                .filter(e -> (EntityUtils.isOtherServerPlayer(e) && players.isEnabled())
                        || (EntityUtils.isFriend(e) && friends.isEnabled())
                        || (EntityUtils.isMob(e) && hostiles.isEnabled())
                        || (EntityUtils.isAnimal(e) && friendly.isEnabled())
                        || ((e instanceof ShulkerBulletEntity || e instanceof AbstractFireballEntity) && projectiles.isEnabled()))
                .sorted(comparator)
                .limit(multiAura.isEnabled() ? 5 : 1L)
                .collect(Collectors.toList());
    }
}