package dev.vili.zyklon.module.modules;

import com.google.common.collect.Sets;
import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import dev.vili.zyklon.setting.settings.ModeSetting;
import dev.vili.zyklon.setting.settings.NumberSetting;
import dev.vili.zyklon.util.InventoryUtils;
import dev.vili.zyklon.util.WorldUtils;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.math.*;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashSet;
import java.util.Set;

public class Scaffold extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", this, "Single", "Single", "3x3", "5x5", "7x7");
    public final NumberSetting delay = new NumberSetting("Delay", this, 4, 1, 10, 1);
    public final NumberSetting range = new NumberSetting("Range", this, 0.3, 0.1, 1, 0.1);
    public final ModeSetting rotate = new ModeSetting("Rotate", this, "Client", "Client", "Packet");
    public final BooleanSetting legit = new BooleanSetting("Legit", this, false);
    public final BooleanSetting tower = new BooleanSetting("Tower", this, true);
    public final BooleanSetting legitTower = new BooleanSetting("LegitTower", this, false);
    public final BooleanSetting airPlace = new BooleanSetting("AirPlace", this, false);
    public final BooleanSetting safeWalk = new BooleanSetting("SafeWalk", this, true);
    public final BooleanSetting noSwing = new BooleanSetting("NoSwing", this, false);
    public final BooleanSetting emptyToggle = new BooleanSetting("EmptyToggle", this, false);

    public Scaffold() {
        super("Scaffold", "Places blocks under you.", GLFW.GLFW_KEY_UNKNOWN, Category.PLAYER);
        this.addSettings(mode, delay, range, rotate, legit, tower, legitTower, airPlace, safeWalk, noSwing, emptyToggle);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.world == null || mc.player == null) return;
        int slot = InventoryUtils.getSlot(false, i -> shouldUseItem(mc.player.getInventory().getStack(i).getItem()));
        int rotMode = 0;
        
        if (slot ==-1) {
            if (emptyToggle.isEnabled()) {
                this.toggle();
            }
            return;
        }

        double r = range.getValue();
        int area = 0;

        if (mode.is("Single")) area = 0;
        else if (mode.is("3x3")) area = 1;
        else if (mode.is("5x5")) area = 2;
        else if (mode.is("7x7")) area = 3;
        

        Vec3d placeVec = mc.player.getPos().add(0, -0.85, 0);
        Set<BlockPos> blocks = area == 0
                ? Sets.newHashSet(
                new BlockPos(placeVec),
                new BlockPos(placeVec.add(range.getValue(), 0, 0)),
                new BlockPos(placeVec.add(-range.getIncrement(), 0, 0)),
                new BlockPos(placeVec.add(0, 0, range.getValue())),
                new BlockPos(placeVec.add(0, 0, -range.getValue())))
                : getSpiral(area, new BlockPos(placeVec)
        );

        if (tower.isEnabled()
                && InputUtil.isKeyPressed(mc.getWindow().getHandle(), InputUtil.fromTranslationKey(mc.options.jumpKey.getBoundKeyTranslationKey()).getCode())) {
            if (mc.world.getBlockState(mc.player.getBlockPos().down()).getMaterial().isReplaceable()
                    && !mc.world.getBlockState(mc.player.getBlockPos().down(2)).getMaterial().isReplaceable()
                    && mc.player.getVelocity().y > 0) {
                mc.player.setVelocity(mc.player.getVelocity().x, -0.1, mc.player.getVelocity().z);

                if (!legitTower.isEnabled()) {
                    mc.player.jump();
                }
            }

            if (legitTower.isEnabled() && mc.player.isOnGround()) {
                mc.player.jump();
            }
        }

        if (blocks.stream().noneMatch(WorldUtils::isBlockEmpty)) return;

        if (rotate.is("Packet")) rotMode = 1;
        else if (rotate.is("Client")) rotMode = 2;

        int cap = 0;
        for (BlockPos bp : blocks) {
            boolean placed = WorldUtils.placeBlock(bp, slot, rotMode, legit.isEnabled(), airPlace.isEnabled(), !noSwing.isEnabled());

            if (placed) {
                cap++;
                if (cap >= delay.getValue()) return;
            }
        }
    }

    private boolean shouldUseItem(Item item) {
        return item instanceof BlockItem;
    }

    private Set<BlockPos> getSpiral(int size, BlockPos center) {
        Set<BlockPos> list = new LinkedHashSet<>();
        list.add(center);

        if (size == 0) return list;

        int step = 1;
        int neededSteps = size * 4;
        BlockPos currentPos = center;
        for (int i = 0; i <= neededSteps; i++) {
            if (i == neededSteps) step--;

            for (int j = 0; j < step; j++) {
                if (i % 4 == 0) currentPos = currentPos.add(-1, 0, 0);
                else if (i % 4 == 1) currentPos = currentPos.add(0, 0, -1);
                else if (i % 4 == 2) currentPos = currentPos.add(1, 0, 0);
                else currentPos = currentPos.add(0, 0, 1);

                list.add(currentPos);
            }

            if (i % 2 != 0) step++;
        }

        return list;
    }
}

