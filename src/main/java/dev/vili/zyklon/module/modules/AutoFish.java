package dev.vili.zyklon.module.modules;

import dev.vili.zyklon.event.events.PacketEvent;
import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.NumberSetting;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import dev.vili.zyklon.eventbus.Subscribe;

public class AutoFish extends Module {
    public final NumberSetting delay = new NumberSetting("Delay", this, 0, 0, 10, 1);

    public AutoFish() {
        super("AutoFish", "Automatically catches fish.", GLFW.GLFW_KEY_UNKNOWN, Category.PLAYER);
        this.addSettings(delay);
    }

    private boolean reeledFish;
    private int ticks;

    @Override
    public void onDisable() {
        reeledFish = false;
        ticks = 0;
    }

    @Subscribe
    public void onPacket(PacketEvent.Receive event) {
        Hand hand = getHandWithRod();
        if (mc.player != null && hand != null && event.getPacket() instanceof PlaySoundS2CPacket
                && SoundEvents.ENTITY_FISHING_BOBBER_SPLASH.equals(((PlaySoundS2CPacket) event.getPacket()).getSound())) {
                reeledFish = true;
        }
    }

    @Subscribe
    public void onTick(TickEvent event) {
        Hand hand = getHandWithRod();
        if (hand != null) {
            if (mc.player.fishHook == null) {
                if (ticks >= delay.getValue()) {
                    ticks = 0;
                    mc.interactionManager.interactItem(mc.player, hand);
                } else ticks++;
            } else if (mc.player.fishHook instanceof FishingBobberEntity) {
                FishingBobberEntity bobber = mc.player.fishHook;
                if (bobber.isInOpenWater() && reeledFish) {
                    reeledFish = false;
                    mc.interactionManager.interactItem(mc.player, hand);
                }
            }
        }
    }


    private Hand getHandWithRod() {
        return mc.player.getMainHandStack().getItem() == Items.FISHING_ROD ? Hand.MAIN_HAND
                : mc.player.getOffHandStack().getItem() == Items.FISHING_ROD ? Hand.OFF_HAND
                : null;
    }

}
