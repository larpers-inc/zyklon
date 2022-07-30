package me.vp.zyklon.module.modules;

import me.vp.zyklon.event.events.PacketEvent;
import me.vp.zyklon.event.events.TickEvent;
import me.vp.zyklon.event.events.WorldRenderEvent;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.BooleanSetting;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemGroup;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import org.quantumclient.energy.Subscribe;

public class NoSlow extends Module {
    public final BooleanSetting blocks = new BooleanSetting("Blocks", this, true);
    public final BooleanSetting items = new BooleanSetting("Items", this, true);
    public final BooleanSetting slowness = new BooleanSetting("Slowness", this, true);
    public final BooleanSetting inventory = new BooleanSetting("Inventory", this, false);

    public NoSlow() {
        super("NoSlow", "Dont slowdown when doing stuff.", GLFW.GLFW_KEY_UNKNOWN, Category.MOVEMENT);
        this.addSettings(blocks, items, slowness, inventory);
    }

    private Vec3d addVelocity = Vec3d.ZERO;
    private long lastTime;

    @Subscribe
    public void onTick(TickEvent event) {
        // Inventory
        if (inventory.isEnabled() && shouldInvMove(mc.currentScreen)) {
            for (KeyBinding key : new KeyBinding[]{mc.options.forwardKey, mc.options.backKey,
                    mc.options.leftKey, mc.options.rightKey, mc.options.jumpKey, mc.options.sprintKey}) {
                key.setPressed(InputUtil.isKeyPressed(mc.getWindow().getHandle(),
                        InputUtil.fromTranslationKey(key.getBoundKeyTranslationKey()).getCode()));
            }
        }

        /* Slowness */
        if (slowness.isEnabled() && (mc.player.getStatusEffect(StatusEffects.SLOWNESS) != null || mc.player.getStatusEffect(StatusEffects.BLINDNESS) != null)) {
            if (mc.options.forwardKey.isPressed()
                    && mc.player.getVelocity().x > -0.15 && mc.player.getVelocity().x < 0.15
                    && mc.player.getVelocity().z > -0.15 && mc.player.getVelocity().z < 0.15) {
                mc.player.setVelocity(mc.player.getVelocity().add(addVelocity));
                addVelocity = addVelocity.add(new Vec3d(0, 0, 0.05).rotateY(-(float) Math.toRadians(mc.player.getYaw())));
            } else {
                addVelocity = addVelocity.multiply(0.75, 0.75, 0.75);
            }
        }

        /* Soul Sand */
        if (blocks.isEnabled() && mc.world.getBlockState(mc.player.getBlockPos()).getBlock() == Blocks.SOUL_SAND) {
            mc.player.setVelocity(mc.player.getVelocity().multiply(2.5, 1, 2.5));
        }

        /* Slime Block */
        if (blocks.isEnabled()
                && mc.world.getBlockState(new BlockPos(mc.player.getPos().add(0, -0.01, 0))).getBlock() == Blocks.SLIME_BLOCK && mc.player.isOnGround()) {
            double d = Math.abs(mc.player.getVelocity().y);
            if (d < 0.1D && !mc.player.bypassesSteppingEffects()) {
                double e = 1 / (0.4D + d * 0.2D);
                mc.player.setVelocity(mc.player.getVelocity().multiply(e, 1.0D, e));
            }
        }
    }

    @Subscribe
    public void onPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof ClickSlotC2SPacket && inventory.isEnabled()) {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
        }
    }

    @Subscribe
    public void onRender(WorldRenderEvent event) {
        if (inventory.isEnabled() && shouldInvMove(mc.currentScreen))
            mc.keyboard.setRepeatEvents(true);
    }

    private boolean shouldInvMove(Screen screen) {
        if (screen == null) return false;

        return !(screen instanceof ChatScreen
                || screen instanceof BookEditScreen
                || screen instanceof SignEditScreen
                || screen instanceof JigsawBlockScreen
                || screen instanceof StructureBlockScreen
                || screen instanceof AnvilScreen
                || (screen instanceof CreativeInventoryScreen
                && ((CreativeInventoryScreen) screen).getSelectedTab() == ItemGroup.SEARCH.getIndex()));
    }
}