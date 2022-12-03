package dev.vili.zyklon.module.modules;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.event.events.PacketEvent;
import dev.vili.zyklon.event.events.RenderEntityEvent;
import dev.vili.zyklon.event.events.RenderIngameHudEvent;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;

import dev.vili.zyklon.setting.settings.ModeSetting;
import dev.vili.zyklon.util.EntityUtils;
import dev.vili.zyklon.util.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import org.lwjgl.glfw.GLFW;

import dev.vili.zyklon.eventbus.Subscribe;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;

public class Hud extends Module {
    public final BooleanSetting watermark = new BooleanSetting("Watermark", this, true);
    public final BooleanSetting arraylist = new BooleanSetting("ArrayList", this, true);
    public final ModeSetting arrayListMode = new ModeSetting("Sort", this, "Reverse", "Reverse", "Normal", "ABC", "Category");
    public final BooleanSetting welcomer = new BooleanSetting("Welcomer", this, true);
    public final BooleanSetting server = new BooleanSetting("Server", this, true);
    public final BooleanSetting players = new BooleanSetting("Players", this, true);
    public final BooleanSetting fps = new BooleanSetting("Fps", this, false);
    public final BooleanSetting ping = new BooleanSetting("Ping", this, false);
    public final BooleanSetting tps = new BooleanSetting("Tps", this, false);
    public final BooleanSetting speed = new BooleanSetting("Speed", this, false);
    public final BooleanSetting effects = new BooleanSetting("Effects", this, true);
    public final BooleanSetting lookingAt = new BooleanSetting("LookingAt", this, true);
    public final BooleanSetting coords = new BooleanSetting("Coords", this, true);
    public final BooleanSetting netherCoords = new BooleanSetting("NetherCoords", this, true);
    public final BooleanSetting facing = new BooleanSetting("Facing", this, false);
    public final BooleanSetting yawPitch = new BooleanSetting("YawPitch", this, false);
    public final BooleanSetting durability = new BooleanSetting("Durability", this, false);
    public final BooleanSetting paperDoll = new BooleanSetting("Paperdoll", this, false);
    public final BooleanSetting targetHud = new BooleanSetting("TargetHud", this, false);
    public final BooleanSetting inventory = new BooleanSetting("Inventory", this, false);
    public final BooleanSetting armor = new BooleanSetting("Armor", this, true);
    public final BooleanSetting rainbow = new BooleanSetting("Rainbow", this, false);
    private PlayerEntity target;
    private boolean found;
    float temp = 10000;
    private int y;
    public long lastPacket = 0;
    private long prevTime = 0;
    private double tpss = 0;

    public Hud() {
        super("Hud", "Renders stuff on screen.", GLFW.GLFW_KEY_UNKNOWN, Category.CLIENT);
        this.addSettings(watermark, arraylist, arrayListMode, welcomer, players, fps, ping, tps, speed, effects, lookingAt, coords, netherCoords, yawPitch, facing, durability,
                paperDoll, targetHud, inventory, armor, rainbow);
    }

    @Subscribe
    public void onRender(RenderIngameHudEvent event) {
        y = 40;
        if (mc.options.debugEnabled) return;

        // Watermark
        if (watermark.isEnabled()) {
            DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, Zyklon.name + " " + Formatting.WHITE + Zyklon.version, 1, 1,
                    rainbow.isEnabled() ? getRainbow() : new Color(0x16733695).getRGB());
        }

        // Welcomer
        if (welcomer.isEnabled()) {
            DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, "Welcome, " + Formatting.WHITE + mc.getSession().getUsername()
                            + Formatting.RESET + "!", 1, y,
                    rainbow.isEnabled() ? getRainbow() : Color.LIGHT_GRAY.getRGB());
            y += 10;
        }

        // Server
        if (server.isEnabled()) {
            String ip = mc.getCurrentServerEntry() != null ? mc.getCurrentServerEntry().address : "Singleplayer";

            DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, "Server [" + Formatting.WHITE + ip + Formatting.RESET + "]", 1, y,
                    rainbow.isEnabled() ? getRainbow() : Color.LIGHT_GRAY.getRGB());
            y += 10;
        }

        // Players
        if (players.isEnabled()) {
            int players = mc.getNetworkHandler().getPlayerList().size();
            DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, "Players [" + Formatting.WHITE + players + Formatting.RESET + "]", 1, y,
                    rainbow.isEnabled() ? getRainbow() : Color.LIGHT_GRAY.getRGB());
            y += 10;
        }

        // Fps
        if (fps.isEnabled()) {
            DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, "FPS [" + Formatting.WHITE + mc.fpsDebugString.split(" ", 2)[0] + Formatting.RESET + "]", 1, y,
                    rainbow.isEnabled() ? getRainbow() : Color.LIGHT_GRAY.getRGB());
            y += 10;
        }

        // Ping
        PlayerListEntry playerEntry = mc.player.networkHandler.getPlayerListEntry(mc.player.getGameProfile().getId());
        int latency = playerEntry == null ? 0 : playerEntry.getLatency();

        if (ping.isEnabled()) {
            DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, "Ping [" + Formatting.WHITE + latency + "ms" + Formatting.RESET + "]", 1, y,
                    rainbow.isEnabled() ? getRainbow() : Color.LIGHT_GRAY.getRGB());
            y += 10;
        }

        // Tps
        if (tps.isEnabled()) {
            DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, "TPS [" + Formatting.WHITE + new DecimalFormat("#.#").format(tpss)
                            + Formatting.RESET + "]", 1, y,
                    rainbow.isEnabled() ? getRainbow() : Color.LIGHT_GRAY.getRGB());
            y += 10;
        }

        // Speed
        if (speed.isEnabled()) {
            final DecimalFormat decimalFormat = new DecimalFormat("#.##");
            Vec3d vec = new Vec3d(mc.player.getX() - mc.player.prevX, 0, mc.player.getZ() - mc.player.prevZ).multiply(20);
            final double speed = Math.abs(vec.length());
            //final double kmh = speed * 3.6;
            final String speedString = "Speed [" + Formatting.WHITE + decimalFormat.format((speed)) + "m/s" + Formatting.RESET + "]";

            DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, speedString, 1, y,
                    rainbow.isEnabled() ? getRainbow() : Color.LIGHT_GRAY.getRGB());
            y += 10;
        }

        // Yaw & Pitch
        if (yawPitch.isEnabled()) {
            final DecimalFormat decimalFormat = new DecimalFormat("#.##");
            int yaw = (int) MathUtil.roundToPlace(mc.player.getYaw(), 1);
            int pitch = (int) MathUtil.roundToPlace(mc.player.getPitch(), 1);

            String yawPitch = "Yaw [" + Formatting.WHITE + yaw + Formatting.RESET + "] Pitch [" + Formatting.WHITE + pitch + Formatting.RESET + "]";
            DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, yawPitch, 1, y, rainbow.isEnabled() ? getRainbow() : Color.LIGHT_GRAY.getRGB());
            y += 10;
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

            final String overWorld = "XYZ [" + Formatting.WHITE
                    + decimalFormat.format(cx) + ", "
                    + decimalFormat.format(cy) + ", "
                    + decimalFormat.format(cz) + Formatting.RESET + "]";
            final String nether = Formatting.DARK_RED + "[" + decimalFormat.format(cx / 8) + ", " + decimalFormat.format(cz / 8) + "]";

            DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, netherCoords.enabled ? overWorld + " " + nether
            : overWorld, 1, mc.getWindow().getScaledHeight() - 10,
                    rainbow.isEnabled() ? getRainbow() : Color.LIGHT_GRAY.getRGB());
        }

        // Facing
        if (facing.isEnabled()) {
            String facing = mc.player.getHorizontalFacing().name().substring(0, 1).toUpperCase()
                            + mc.player.getHorizontalFacing().name().substring(1).toLowerCase();
            String axis = switch (facing) {
                case "North" -> "-Z";
                case "South" -> "+Z";
                case "East" -> "+X";
                case "West" -> "-X";
                default -> "?";
            };

            DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, facing + " " + "[" + Formatting.WHITE + axis + Formatting.RESET + "]", 1, coords.enabled ? mc.getWindow().getScaledHeight() - 20
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
            String text = "Durability [" + Formatting.WHITE + percent + "%" + Formatting.RESET + "]";

            DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, text, 1, y,
                    rainbow.isEnabled() ? getRainbow() : Color.LIGHT_GRAY.getRGB());
            y += 10;
        }

        // Effects
        if (effects.isEnabled()) {
            int x = 1;
            for (StatusEffectInstance effectInstance : mc.player.getStatusEffects()) {
                StatusEffect effect = effectInstance.getEffectType();
                String name = effect.getName().getString();
                String amplifier = String.valueOf(effectInstance.getAmplifier() + 1);
                String duration = effectInstance.getDuration() / 20 + "s";
                int color = effect.getColor();
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = color & 0xFF;

                String text = name + " " + amplifier + " " + duration;

                DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, text, x, y,
                        rainbow.isEnabled() ? getRainbow() : new Color(r, g, b).getRGB());
                    y += 10;
            }
            y += 10;
        }

        // ArrayList
        int iteration = 0;
        if (arraylist.isEnabled()) {
            List<Module> mod = Zyklon.INSTANCE.moduleManager.getEnabledModules();
            if (arrayListMode.is("Reverse"))
                mod.sort(Comparator.comparingInt(m -> mc.textRenderer.getWidth(((Module) m).getName())).reversed());
            else if (arrayListMode.is("Normal"))
                mod.sort(Comparator.comparingInt(m -> mc.textRenderer.getWidth(m.getName())));
            else if (arrayListMode.is("ABC"))
                mod.sort(Comparator.comparing(m -> m.getName()));
            else if (arrayListMode.is("Category"))
                mod.sort(Comparator.comparing(m -> m.getCategory().name()));

            for (Module m : mod) {
                if (m.hided.isEnabled()) continue; {
                    String key = InputUtil.fromKeyCode(m.getKey(), -1).getLocalizedText().getString();
                    String txt = m.getName() + " [" + Formatting.WHITE + key + Formatting.RESET + "]";
                    String txt2 = m.getName();

                    DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, m.getKey() == GLFW.GLFW_KEY_UNKNOWN ? txt2 : txt,
                            mc.getWindow().getScaledWidth() - mc.textRenderer.getWidth(m.getKey() == GLFW.GLFW_KEY_UNKNOWN ? txt2 : txt),
                            1 + (iteration * 10),
                            rainbow.isEnabled() ? getRainbow() : new Color(0x16733695).brighter().getRGB());
                    iteration++;
                }
            }
        }

        // Paper-doll
        if (paperDoll.isEnabled()) {
            if (!(mc.player == null)) {
                float yaw = MathHelper.wrapDegrees(mc.player.prevYaw + (mc.player.getYaw() - mc.player.prevYaw) * mc.getTickDelta());
                float pitch = mc.player.getPitch();
                event.getMatrix().push();
                InventoryScreen.drawEntity(arraylist.enabled ? mc.getWindow().getScaledWidth() - 85 : mc.getWindow().getScaledWidth() - 20, 50, 25, -yaw, -pitch, mc.player);
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

        // LookingAt
        if (lookingAt.isEnabled()) {
            if (mc.crosshairTarget != null) {
                if (mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                    BlockPos pos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
                    BlockState state = mc.world.getBlockState(pos);
                    Block block = state.getBlock();
                    String name = block.getName().getString();

                    mc.getItemRenderer().renderGuiItemIcon(new ItemStack(block), mc.getWindow().getScaledWidth() / 2 - mc.textRenderer.getWidth(name) / 2 - 20, 10);
                    DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, name,
                            mc.getWindow().getScaledWidth() / 2 - mc.textRenderer.getWidth(name) / 2, 10, rainbow.isEnabled() ? getRainbow() : Color.LIGHT_GRAY.getRGB());
                }
            }
        }

        // TargetHud
        if (targetHud.isEnabled()) {
            if (target == null || target == mc.player) return;
            if (target.isDead()) return;
            if (EntityUtils.isFriend(target)) return;

            int x = mc.getWindow().getScaledWidth() - 280;
            int y = mc.getWindow().getScaledHeight() - 65;
            String info = target.getEntityName() + " | " + EntityUtils.getEntityPing(target) + "ms" + " | " + EntityUtils.getEntityGamemode(target);
            String info2 = String.format("%.1f", target.getHealth() + target.getAbsorptionAmount()) + " health"
                    + " | " + String.format("%.1f", mc.player.distanceTo(target)) + "m";

            if (target != null) {
                DrawableHelper.fill(event.getMatrix(), x, y, x + 150, y + 70, new Color(0, 0, 0, 100).getRGB());
                DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, info, x + 10, y + 9, Color.WHITE.getRGB());
                DrawableHelper.drawStringWithShadow(event.getMatrix(), mc.textRenderer, info2, x + 10, y + 20, Color.WHITE.getRGB());

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

    @Subscribe
    public void onPacket(PacketEvent.Receive event) {
        lastPacket = System.currentTimeMillis();

        if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
            long time = System.currentTimeMillis();
            long timeOffset = Math.abs(1000 - (time-prevTime)) + 1000;
            tpss = (MathHelper.clamp(20 / (timeOffset /1000d), 0, 20) * 100d)/100d;
            prevTime = time;
        }
    }

    private Color getColor(float max, float value) {
        double percent = 100 / (max / value);
        if (percent <= 30) return Color.RED;
        if (30 < percent && percent <= 70) return Color.YELLOW;
        if (percent > 70) return Color.GREEN;
        return null;
    }

    private int getRainbow() {
        int hue = MathHelper.floor((System.currentTimeMillis() % 5000L) / 5000.0F * 360.0F);
        return Color.HSBtoRGB(hue / 360.0F, 1.0F, 1.0F);
    }

    private int getWidth(float value) {
        double percent = 100 / (target.getMaxHealth() / value);
        return (int) (170 / 100 * percent);
    }
}