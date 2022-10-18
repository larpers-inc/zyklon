package me.vp.zyklon.module.modules;

import com.mojang.blaze3d.systems.RenderSystem;

import me.vp.zyklon.Zyklon;
import me.vp.zyklon.event.events.RenderEntityEvent;
import me.vp.zyklon.event.events.RenderIngameHudEvent;
import me.vp.zyklon.module.Module;
import me.vp.zyklon.setting.settings.BooleanSetting;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import org.lwjgl.glfw.GLFW;

import me.vp.zyklon.eventbus.Subscribe;

import java.awt.*;
import java.text.DecimalFormat;

public class Hud extends Module {
    public final BooleanSetting watermark = new BooleanSetting("Watermark", this, true);
    public final BooleanSetting arraylist = new BooleanSetting("ArrayList", this, true);
    public final BooleanSetting fps = new BooleanSetting("Fps", this, false);
    public final BooleanSetting ping = new BooleanSetting("Ping", this, false);
    public final BooleanSetting speed = new BooleanSetting("Speed", this, false);
    public final BooleanSetting coords = new BooleanSetting("Coords", this, true);
    public final BooleanSetting netherCoords = new BooleanSetting("NetherCoords", this, true);
    public final BooleanSetting facing = new BooleanSetting("Facing", this, false);
    public final BooleanSetting durability = new BooleanSetting("Durability", this, false);
    public final BooleanSetting paperDoll = new BooleanSetting("Paperdoll", this, false);
    public final BooleanSetting targetHud = new BooleanSetting("TargetHud", this, false);
    public final BooleanSetting inventory = new BooleanSetting("Inventory", this, false);
    public final BooleanSetting armor = new BooleanSetting("Armor", this, true);
    public final BooleanSetting rainbow = new BooleanSetting("Rainbow", this, false);
    private PlayerEntity target;
    private boolean found;
    float temp = 10000;

    public Hud() {
        super("Hud", "Renders stuff on screen.", GLFW.GLFW_KEY_UNKNOWN, Category.CLIENT);
        this.addSettings(watermark, arraylist, fps, ping, speed, coords, netherCoords, facing, durability, paperDoll, targetHud, inventory, armor, rainbow);
    }

    @Subscribe
    public void onRender(RenderIngameHudEvent event) {
        if (mc.options.debugEnabled) return;

        // Watermark
        if (watermark.isEnabled()) {
            DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, Zyklon.name + " " + Zyklon.version, 1, 1,
                    rainbow.isEnabled() ? getRainbow() : 0x64b9fa);
        }


        // Fps
        if (fps.isEnabled()) {
            DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, "FPS [" + mc.fpsDebugString.split(" ", 2)[0] + "]", 1, 60,
                    rainbow.isEnabled() ? getRainbow() : Color.LIGHT_GRAY.getRGB());
        }

        // Ping
        PlayerListEntry playerEntry = mc.player.networkHandler.getPlayerListEntry(mc.player.getGameProfile().getId());
        int latency = playerEntry == null ? 0 : playerEntry.getLatency();

        if (ping.isEnabled()) {
            DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, "Ping [" + latency + "ms]", 1, 70,
                    rainbow.isEnabled() ? getRainbow() : Color.LIGHT_GRAY.getRGB());
        }

        // Speed
        if (speed.isEnabled()) {
            final DecimalFormat decimalFormat = new DecimalFormat("#.#");
            Vec3d vec = new Vec3d(mc.player.getX() - mc.player.prevX, 0, mc.player.getZ() - mc.player.prevZ).multiply(20);
            final double speed = Math.abs(vec.length());
            final String speedString = "Speed [" + decimalFormat.format((speed)) + "km/h]";

            DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, speedString, 1, 80,
                    rainbow.isEnabled() ? getRainbow() : Color.LIGHT_GRAY.getRGB());
        }

        // Coords
        if (coords.isEnabled()) {
            final DecimalFormat decimalFormat = new DecimalFormat("###.#");
            double cx = mc.player.getX();
            double cy = mc.player.getY();
            double cz = mc.player.getZ();

            if (mc.world.getDimension().respawnAnchorWorks()) {
                cx *= 8;
                cz *= 8;
            }

            final String overWorld = "XYZ [" + decimalFormat.format(cx) + ", " + decimalFormat.format(cy) + ", " + decimalFormat.format(cz) + "]";
            final String nether = Formatting.DARK_RED + "[" + decimalFormat.format(cx / 8) + ", " + decimalFormat.format(cz / 8) + "]";

            DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, netherCoords.enabled ? overWorld + " " + nether
            : overWorld, 1, mc.getWindow().getScaledHeight() - 10,
                    rainbow.isEnabled() ? getRainbow() : Color.LIGHT_GRAY.getRGB());
        }

        // Facing
        if (facing.isEnabled()) {
            String facing = mc.player.getHorizontalFacing().name().substring(0, 1).toUpperCase()
                            + mc.player.getHorizontalFacing().name().substring(1).toLowerCase();
            String axis = mc.player.getHorizontalFacing().getAxis().asString();

            DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, facing + " " + "[" + axis + "]", 1, coords.enabled ? mc.getWindow().getScaledHeight() - 20
            : mc.getWindow().getScaledHeight() - 10,
                    rainbow.isEnabled() ? getRainbow() : Color.LIGHT_GRAY.getRGB());
        }

        // Durability
        if (durability.isEnabled()) {
            ItemStack itemStack = mc.player.getMainHandStack();
            int maxDamage = itemStack.getMaxDamage();
            int damage = itemStack.getDamage();
            int durability = maxDamage - damage;
            int percent = (int) Math.round((double) durability / (double) maxDamage * 100);
            String text = "Durability [" + percent + "%]";

            DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, text, 1, 90,
                    rainbow.isEnabled() ? getRainbow() : Color.LIGHT_GRAY.getRGB());
        }

        // ArrayList
        int iteration = 0;
        if (arraylist.isEnabled()) {
            for (int i = 0; i < Zyklon.INSTANCE.moduleManager.modules.size(); i++) {
                Module mod = Zyklon.INSTANCE.moduleManager.modules.get(i);
                if (mod.isEnabled()) {
                    DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, mod.getName(),
                                                        mc.getWindow().getScaledWidth() - mc.textRenderer.getWidth(mod.getName()), 1 + (iteration * 10),
                            rainbow.isEnabled() ? getRainbow() : 0x64b9fa);
                    iteration++;
                }
            }
        }

        // Paperdoll
        if (paperDoll.isEnabled()) {
            if (!(mc.player == null)) {
                float yaw = MathHelper.wrapDegrees(mc.player.prevYaw + (mc.player.getYaw() - mc.player.prevYaw) * mc.getTickDelta());
                float pitch = mc.player.getPitch();
                event.getMatrix().push();
                InventoryScreen.drawEntity(arraylist.enabled ? mc.getWindow().getScaledWidth() - 80 : mc.getWindow().getScaledWidth() - 20, 50, 25, -yaw, -pitch, mc.player);
                RenderSystem.enableDepthTest();
                event.getMatrix().pop();
            }
        }

        // Armor
        if (armor.isEnabled()) {
            int x = mc.getWindow().getScaledWidth() - 470;
            int y = mc.getWindow().getScaledHeight() - 60;
            for (int count = 0; count < mc.player.getInventory().armor.size(); count++) {
                ItemStack is = mc.player.getInventory().armor.get(count);

                if (is.isEmpty())
                    continue;

                int curX = x + count * 20;
                mc.getItemRenderer().renderGuiItemIcon(is, curX, y + 4);
                int durColor = is.isDamageable() ? 0xff000000 | MathHelper.hsvToRgb(((float) (is.getMaxDamage() - is.getDamage()) / is.getMaxDamage()) / 3.0F, 1.0F, 1.0F) : 0;

                if (is.isDamaged()) {
                    int barLength = Math.round(13.0F - is.getDamage() * 13.0F / is.getMaxDamage());
                    DrawableHelper.fill(event.getMatrix(), curX + 2, y + 17, curX + 15, y + 19, 0xff000000);
                    DrawableHelper.fill(event.getMatrix(), curX + 2, y + 17, curX + 2 + barLength, y + 18, durColor);
                }

            }
        }

        // Inventory
        if (inventory.isEnabled()) {
            int x = mc.getWindow().getScaledWidth() - 550;
            int y = mc.getWindow().getScaledHeight() - 120;

            DrawableHelper.fill(event.getMatrix(), x + 145, y, x, y + 50, new Color(0, 0, 0, 100).getRGB());

            for (int i = 0; i < 27; i++) {
                ItemStack itemStack = mc.player.getInventory().main.get(i + 9);
                int offSetX = x + (i % 9) * 16;
                int offSetY = y + (i / 9) * 16;
                mc.getItemRenderer().renderGuiItemIcon(itemStack, offSetX, offSetY);
                mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, itemStack, offSetX, offSetY);
            }
            mc.getItemRenderer().zOffset = 0.0F;
        }

        // TargetHud
        if (targetHud.isEnabled()) {
            if (target == null) return;

            int x = mc.getWindow().getScaledWidth() - 280;
            int y = mc.getWindow().getScaledHeight() - 65;
            PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(target.getUuid());
            int targetLatency = playerListEntry == null ? 0 : playerListEntry.getLatency();
            String info = target.getEntityName() + " | " + targetLatency + "ms";
            String health = String.format("%.1f", target.getHealth() + target.getAbsorptionAmount()) + " health";
            String location = String.format("%.1f", mc.player.distanceTo(target)) + "m";

            if (target != null) {
                DrawableHelper.fill(event.getMatrix(), x, y, x + 150, y + 70, new Color(0, 0, 0, 100).getRGB());
                DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, info, x + 10, y + 9, Color.WHITE.getRGB());
                DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, health, x + 10, y + 20, Color.WHITE.getRGB());
                DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, location, x + 90, y + 20, Color.WHITE.getRGB());

                int i = 1;
                for (ItemStack item : target.getArmorItems()) {
                    mc.getItemRenderer().renderGuiItemIcon(item, x + (9 * i) + (i * 9) - 9, y + 30);
                    i++;
                }

                mc.getItemRenderer().renderGuiItemIcon(target.getMainHandStack(), x + 80, y + 30);
                mc.getItemRenderer().renderGuiItemIcon(target.getOffHandStack(), x + 100, y + 30);
                InventoryScreen.drawEntity(x + 130, y + 62, 25, -MathHelper.wrapDegrees(target.prevYaw + (target.getYaw() - target.prevYaw) * mc.getTickDelta()), -target.getPitch(), target);
                DrawableHelper.fill(event.getMatrix(), x + 5, y + 50, x + getWidth(target.getAbsorptionAmount() + target.getHealth()) + 10, y + 60,
                                    getColor(36, 100 / 36f * target.getHealth() + target.getAbsorptionAmount()).getRGB());
            }
        }
    }

    @Subscribe
    public void onRender(RenderEntityEvent event) {
        for (AbstractClientPlayerEntity player : mc.world.getPlayers()) {
            if (mc.player.distanceTo(player) < 150 && mc.player.distanceTo(player) < temp && player != mc.player) {
                target = player;
                found = true;
                temp = mc.player.distanceTo(player);
            }
        }
        if (!found) target = null;
        else found = false;
        temp = 10000;
    }

    private Color getColor(float max, float value) {
        double percent = 100 / (max / value);
        if (percent <= 30) return Color.RED;
        if (30 < percent && percent <= 70) return Color.YELLOW;
        if (percent > 70) return Color.GREEN;
        return null;
    }

    private static int getRainbow() {
        double rainbowState = Math.ceil((System.currentTimeMillis() + 5) / 2) % 360;
        return 0xff000000 | MathHelper.hsvToRgb((float) (rainbowState / 360.0), 1f, 1f);
    }

    private int getWidth(float value) {
        double percent = 100 / (target.getMaxHealth() / value);
        return (int) (170 / 100 * percent);
    }
}