package dev.vili.zyklon.module.modules;

import com.google.common.collect.Streams;
import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import dev.vili.zyklon.setting.settings.NumberSetting;
import dev.vili.zyklon.util.EntityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import dev.vili.zyklon.eventbus.Subscribe;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AutoIgnite extends Module {
    public final BooleanSetting players = new BooleanSetting("Players", this, true);
    public final BooleanSetting friends = new BooleanSetting("Friends", this, false);
    public final BooleanSetting hostiles = new BooleanSetting("Hostiles", this, false);
    public final BooleanSetting friendly = new BooleanSetting("Friendly", this, true);
    public final NumberSetting range = new NumberSetting("Range", this, 3, 0, 5, 0.5);
    Hand hand = null;

    public AutoIgnite() {
        super("AutoIgnite", "Automatically ignites entities.", GLFW.GLFW_KEY_UNKNOWN, Category.COMBAT);
        this.addSettings(players, hostiles, friendly, friends, range);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;

        ItemStack mainHandStack = mc.player.getMainHandStack();
        ItemStack offHandStack = mc.player.getOffHandStack();
        hand = null;

        if (mainHandStack != null && (mainHandStack.getItem() == Items.FLINT_AND_STEEL || mainHandStack.getItem() == Items.FIRE_CHARGE))
            hand = Hand.MAIN_HAND;
        if (offHandStack != null && (offHandStack.getItem() == Items.FLINT_AND_STEEL || offHandStack.getItem() == Items.FIRE_CHARGE))
            hand = Hand.OFF_HAND;
        if (hand == null) return;

        try {
            for (Entity entity : getTargets()) {
                if (entity != null && entity.isLiving()) {
                    Block footBlock = mc.world.getBlockState(entity.getBlockPos().down()).getBlock();
                    BlockPos blockPos = entity.getBlockPos();

                    if (footBlock == Blocks.AIR) blockPos = entity.getBlockPos().down();

                    if (footBlock != null) {
                        Vec3d pos = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                        BlockHitResult hitResult = new BlockHitResult(pos, Direction.UP, blockPos.down(), false);
                        mc.interactionManager.interactBlock(mc.player, hand, hitResult);
                    }
                }
            }

        } catch (Exception ignored) {
        }
    }


    private List<Entity> getTargets() {
        Stream<Entity> targets;
        targets = Streams.stream(mc.world.getEntities());
        Comparator<Entity> comparator;
        comparator = Comparator.comparing(mc.player::distanceTo);

        return targets
                .filter(e -> !e.isFireImmune() && e.isOnGround() && !e.isOnFire()
                        && mc.player.distanceTo(e) <= range.getValue())
                .filter(e -> (EntityUtils.isOtherServerPlayer(e) && players.isEnabled())
                        || (EntityUtils.isFriend(e) && friends.isEnabled())
                        || (EntityUtils.isMob(e) && hostiles.isEnabled())
                        || (EntityUtils.isAnimal(e) && friendly.isEnabled()))
                .sorted(comparator)
                .limit(1L)
                .collect(Collectors.toList());
    }
}
