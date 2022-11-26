package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import dev.vili.zyklon.setting.settings.NumberSetting;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class AutoArmor extends Module {
    public final BooleanSetting antiBreak = new BooleanSetting("AntiBreak", this, true);
    public final BooleanSetting preferElytra = new BooleanSetting("PreferElytra", this, true);
    public final NumberSetting delay = new NumberSetting("Delay", this,0, 0, 10, 1);

    public AutoArmor() {
        super("AutoArmor", "Automatically equips the best armor", GLFW.GLFW_KEY_UNKNOWN, Category.COMBAT);
        this.addSettings(antiBreak, preferElytra, delay);
    }

    int ticks;

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player.playerScreenHandler != mc.player.currentScreenHandler) return;

        if (ticks > 0) {
            ticks--;
            return;
        }

        ticks = (int) delay.getValue();

        /* [Slot type, [Armor slot, Armor prot, New armor slot, New armor prot]] */
        Map<EquipmentSlot, int[]> armorMap = new HashMap<>(4);
        armorMap.put(EquipmentSlot.FEET, new int[] { 36, getProtection(mc.player.getInventory().getStack(36)), -1, -1 });
        armorMap.put(EquipmentSlot.LEGS, new int[] { 37, getProtection(mc.player.getInventory().getStack(37)), -1, -1 });
        armorMap.put(EquipmentSlot.CHEST, new int[] { 38, getProtection(mc.player.getInventory().getStack(38)), -1, -1 });
        armorMap.put(EquipmentSlot.HEAD, new int[] { 39, getProtection(mc.player.getInventory().getStack(39)), -1, -1 });

        if (antiBreak.isEnabled()) {
            for (Map.Entry<EquipmentSlot, int[]> e : armorMap.entrySet()) {
                ItemStack is = mc.player.getInventory().getStack(e.getValue()[0]);
                int armorSlot = (e.getValue()[0] - 34) + (39 - e.getValue()[0]) * 2;

                if (is.isDamageable() && is.getMaxDamage() - is.getDamage() < 7) {
                    /* Look for an empty slot to quick move to */
                    int forceMoveSlot = -1;
                    for (int s = 0; s < 36; s++) {
                        if (mc.player.getInventory().getStack(s).isEmpty()) {
                            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, armorSlot, 1, SlotActionType.QUICK_MOVE, mc.player);
                            return;
                        } else if (!(mc.player.getInventory().getStack(s).getItem() instanceof ToolItem)
                                && !(mc.player.getInventory().getStack(s).getItem() instanceof ArmorItem)
                                && !(mc.player.getInventory().getStack(s).getItem() instanceof ElytraItem)
                                && mc.player.getInventory().getStack(s).getItem() != Items.TOTEM_OF_UNDYING && forceMoveSlot == -1) {
                            forceMoveSlot = s;
                        }
                    }

                    /* Bruh no empty spots, then force move to a non-totem/tool/armor item */
                    if (forceMoveSlot != -1) {
                        //System.out.println(forceMoveSlot);
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId,
                                forceMoveSlot < 9 ? 36 + forceMoveSlot : forceMoveSlot, 1, SlotActionType.THROW, mc.player);
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, armorSlot, 1, SlotActionType.QUICK_MOVE, mc.player);
                        return;
                    }

                    /* No spots to move to, yeet the armor to not cause any bruh moments */
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, armorSlot, 1, SlotActionType.THROW, mc.player);
                    return;
                }
            }
        }

        for (int s = 0; s < 36; s++) {
            int prot = getProtection(mc.player.getInventory().getStack(s));

            if (prot > 0) {
                EquipmentSlot slot = (mc.player.getInventory().getStack(s).getItem() instanceof ElytraItem
                        ? EquipmentSlot.CHEST : ((ArmorItem) mc.player.getInventory().getStack(s).getItem()).getSlotType());

                for (Map.Entry<EquipmentSlot, int[]> e: armorMap.entrySet()) {
                    if (e.getKey() == slot) {
                        if (prot > e.getValue()[1] && prot > e.getValue()[3]) {
                            e.getValue()[2] = s;
                            e.getValue()[3] = prot;
                        }
                    }
                }
            }
        }

        for (Map.Entry<EquipmentSlot, int[]> e: armorMap.entrySet()) {
            if (e.getValue()[2] != -1) {
                if (e.getValue()[1] == -1 && e.getValue()[2] < 9) {
                    if (e.getValue()[2] != mc.player.getInventory().selectedSlot) {
                        mc.player.getInventory().selectedSlot = e.getValue()[2];
                        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(e.getValue()[2]));
                    }

                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 36 + e.getValue()[2], 1, SlotActionType.QUICK_MOVE, mc.player);
                } else if (mc.player.playerScreenHandler == mc.player.currentScreenHandler) {
                    /* Convert inventory slots to container slots */
                    int armorSlot = (e.getValue()[0] - 34) + (39 - e.getValue()[0]) * 2;
                    int newArmorslot = e.getValue()[2] < 9 ? 36 + e.getValue()[2] : e.getValue()[2];

                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, newArmorslot, 0, SlotActionType.PICKUP, mc.player);
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, armorSlot, 0, SlotActionType.PICKUP, mc.player);

                    if (e.getValue()[1] != -1)
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, newArmorslot, 0, SlotActionType.PICKUP, mc.player);
                }

                return;
            }
        }
    }


    private int getProtection(ItemStack is) {
        if (is.getItem() instanceof ArmorItem || is.getItem() == Items.ELYTRA) {
            int prot = 0;

            if (is.getItem() instanceof ElytraItem) {
                if (!ElytraItem.isUsable(is)) return 0;
                if (preferElytra.isEnabled()) prot = 32767;
                else prot = 1;

            }
            else if (is.getMaxDamage() - is.getDamage() < 7 && antiBreak.isEnabled()) return 0;

            if (is.hasEnchantments()) {
                for (Map.Entry<Enchantment, Integer> e: EnchantmentHelper.get(is).entrySet()) {
                    if (e.getKey() instanceof ProtectionEnchantment) prot += e.getValue();
                }
            }

            return (is.getItem() instanceof ArmorItem ? ((ArmorItem) is.getItem()).getProtection() : 0) + prot;
        } else if (!is.isEmpty()) {
            return 0;
        }

        return -1;
    }
}
