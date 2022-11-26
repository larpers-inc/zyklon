package dev.vili.zyklon.module.modules;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dev.vili.zyklon.event.events.RenderEntityEvent;
import dev.vili.zyklon.event.events.TickEvent;
import dev.vili.zyklon.event.events.WorldRenderEvent;
import dev.vili.zyklon.eventbus.Subscribe;
import dev.vili.zyklon.module.Module;
import dev.vili.zyklon.setting.settings.BooleanSetting;
import dev.vili.zyklon.util.EntityUtils;
import dev.vili.zyklon.util.RenderUtils;
import dev.vili.zyklon.util.WorldRenderUtils;
import dev.vili.zyklon.util.ZLogger;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Nametags extends Module {
    public final BooleanSetting players = new BooleanSetting("Players", this, true);
    public final BooleanSetting friendly = new BooleanSetting("FriendlyEntities", this, false);
    public final BooleanSetting hostile = new BooleanSetting("HostileEntities", this, false);
    public final BooleanSetting items = new BooleanSetting("ItemEntities", this, false);
    public final BooleanSetting projectiles = new BooleanSetting("ProjectileEntities", this, false);

    public final BooleanSetting health = new BooleanSetting("Health", this, true);
    public final BooleanSetting ping = new BooleanSetting("Ping", this, true);
    public final BooleanSetting distance = new BooleanSetting("Distance", this, true);
    public final BooleanSetting armor = new BooleanSetting("Armor", this, true);
    public final BooleanSetting babyStatus = new BooleanSetting("BabyStatus", this, false);

    private ExecutorService uuidExecutor;
    private final Map<UUID, Future<String>> uuidFutures = new HashMap<>();
    private final Queue<UUID> uuidQueue = new ArrayDeque<>();
    private final Map<UUID, String> uuidCache = new HashMap<>();
    private final Set<UUID> failedUUIDs = new HashSet<>();
    private long lastLookup = 0;

    public Nametags() {
        super("Nametags", "Better nametags.", GLFW.GLFW_KEY_UNKNOWN, Category.RENDER);
        this.addSettings(players, friendly, hostile, items, projectiles, health, ping, distance, armor, babyStatus);
    }

    @Override
    public void onDisable() {
        uuidQueue.clear();
        failedUUIDs.clear();
        uuidExecutor.shutdownNow();
        uuidFutures.clear();

        Map<UUID, String> cacheCopy = new HashMap<>(uuidCache);
        uuidCache.clear();

        cacheCopy.forEach((u, s) -> {
            if (!s.startsWith("\u00a7c")) uuidCache.put(u, s);
        });
    }

    @Override
    public void onEnable() {
        uuidExecutor = Executors.newFixedThreadPool(4);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        for (Map.Entry<UUID, Future<String>> f: new HashMap<>(uuidFutures).entrySet()) {
            if (f.getValue().isDone()) {
                try {
                    String s = f.getValue().get();
                    uuidCache.put(f.getKey(), s);

                    uuidFutures.remove(f.getKey());
                } catch (InterruptedException | ExecutionException ignored) {

                }
            }
        }
        if (!uuidQueue.isEmpty() && System.currentTimeMillis() - lastLookup > 1000) {
            lastLookup=System.currentTimeMillis();
            addUUIDFuture(uuidQueue.poll());
        }
    }

    @Subscribe
    public void onLivingLabelRender(RenderEntityEvent.Single.Label event) {
        if (event.getEntity() instanceof PlayerEntity) event.setCancelled(true);
    }

    @Subscribe
    public void onRender(WorldRenderEvent.Post event) {
        for (Entity entity: mc.world.getEntities()) {
            if (entity == mc.player || entity.hasPassenger(mc.player) || mc.player.hasPassenger(entity)) continue;

            Vec3d rPos = entity.getPos().subtract(RenderUtils.getInterpolationOffset(entity)).add(0, entity.getHeight() + 0.25, 0);
            double scale = Math.max(3 * (mc.cameraEntity.distanceTo(entity) / 20), 1);

            if (players.isEnabled() && entity instanceof PlayerEntity) {
                List<Text> lines = getPlayerNametags((PlayerEntity) entity);
                drawLines(rPos.x, rPos.y, rPos.z, scale, lines);
            }
            else if (friendly.isEnabled() && entity instanceof AnimalEntity) {
                List<Text> lines = getFriendlyEntityNametags((AnimalEntity) entity);
                drawLines(rPos.x, rPos.y, rPos.z, scale, lines);
            }
            else if (hostile.isEnabled() && entity instanceof HostileEntity) {
                List<Text> lines = getHostileEntityNametags((HostileEntity) entity);
                drawLines(rPos.x, rPos.y, rPos.z, scale, lines);
            }
            else if (items.isEnabled() && entity instanceof ItemEntity) {
                List<Text> lines = getItemEntityNametags((ItemEntity) entity);
                drawLines(rPos.x, rPos.y, rPos.z, scale, lines);
            }
            else if (projectiles.isEnabled() && entity instanceof ProjectileEntity) {
                List<Text> lines = getProjectileEntityNametags((ProjectileEntity) entity);
                drawLines(rPos.x, rPos.y, rPos.z, scale, lines);
            }
        }
    }

    public List<Text> getPlayerNametags(PlayerEntity player) {
        if (!players.isEnabled()) return null;
        List<Text> lines = new ArrayList<>();
        List<Text> mainText = new ArrayList<>();
        PlayerListEntry playerEntry = mc.player.networkHandler.getPlayerListEntry(player.getGameProfile().getId());
        double scale = Math.max(3 * (mc.cameraEntity.distanceTo(player) / 20), 1);

        if (playerEntry != null && ping.isEnabled()) mainText.add(Text.literal(playerEntry.getLatency() + "ms").formatted(Formatting.GRAY));

        mainText.add(((MutableText) player.getName()).formatted(EntityUtils.isFriend(player) ? Formatting.BLUE : Formatting.WHITE));

        if (health.isEnabled()) mainText.add(getHealthText(player));
        if (distance.isEnabled()) mainText.add(getDistanceText(player));
        if (armor.isEnabled()) drawItems(player.getX(), player.getY() + player.getHeight() + 0.75, player.getZ(), scale, getMainEquipment(player));

        lines.add(Texts.join(mainText, Text.literal(" ")));

        return lines;
    }

    public List<Text> getFriendlyEntityNametags(AnimalEntity entity) {
        if (!friendly.isEnabled()) return null;
        List<Text> lines = new ArrayList<>();
        List<Text> mainText = new ArrayList<>();

        mainText.add(((MutableText) entity.getName()).formatted(Formatting.WHITE));

        if (babyStatus.isEnabled()) mainText.add(getBabyStatusText(entity));
        if (health.isEnabled()) mainText.add(getHealthText(entity));
        if (distance.isEnabled()) mainText.add(getDistanceText(entity));

        lines.add(Texts.join(mainText, Text.literal(" ")));

        return lines;
    }

    public List<Text> getHostileEntityNametags(HostileEntity entity) {
        if (!hostile.isEnabled()) return null;
        List<Text> lines = new ArrayList<>();
        List<Text> mainText = new ArrayList<>();
        double scale = Math.max(3 * (mc.cameraEntity.distanceTo(entity) / 20), 1);

        mainText.add(((MutableText) entity.getName()).formatted(Formatting.WHITE));

        if (health.isEnabled()) mainText.add(getHealthText(entity));
        if (distance.isEnabled()) mainText.add(getDistanceText(entity));
        if (armor.isEnabled()) drawItems(entity.getX(), entity.getY() + entity.getHeight() + 0.75, entity.getZ(), scale, getMainEquipment(entity));

        lines.add(Texts.join(mainText, Text.literal(" ")));

        return lines;
    }

    public List<Text> getItemEntityNametags(ItemEntity entity) {
        if (!items.isEnabled()) return null;
        List<Text> lines = new ArrayList<>();
        List<Text> mainText = new ArrayList<>();

        mainText.add(((MutableText) entity.getName()).formatted(Formatting.WHITE));

        if (distance.isEnabled()) mainText.add(getDistanceText(entity));

        lines.add(Texts.join(mainText, Text.literal(" ")));

        return lines;
    }

    public List<Text> getProjectileEntityNametags(ProjectileEntity entity) {
        if (!projectiles.isEnabled()) return null;
        List<Text> lines = new ArrayList<>();
        List<Text> mainText = new ArrayList<>();

        mainText.add(((MutableText) entity.getName()).formatted(Formatting.WHITE));

        if (distance.isEnabled()) mainText.add(getDistanceText(entity));

        lines.add(Texts.join(mainText, Text.literal(" ")));

        return lines;
    }

    /* -------------- Text ------------------ */

    private Text getHealthText(LivingEntity entity) {
        int totalHealth = (int) (entity.getHealth() + entity.getAbsorptionAmount());
        return Text.literal(Integer.toString(totalHealth)).styled(s -> s.withColor(getHealthColor(entity)));
    }

    private int getHealthColor(LivingEntity entity) {
        if (entity.getHealth() + entity.getAbsorptionAmount() > entity.getMaxHealth()) {
            return Formatting.YELLOW.getColorValue();
        } else {
            return MathHelper.hsvToRgb((entity.getHealth() + entity.getAbsorptionAmount()) / (entity.getMaxHealth() * 3), 1f, 1f);
        }
    }

    private Text getDistanceText(Entity entity) {
        double distance = mc.player.distanceTo(entity);
        return Text.literal(String.format("%.1f", distance) + "m").formatted(Formatting.GRAY);
    }

    private Text getBabyStatusText(AnimalEntity entity) {
        if (entity != null) {
            return Text.literal((entity).isBaby() ? "Baby" : "Adult").formatted(Formatting.GRAY);
        }
        return null;
    }


    /*--------------- Rendering stuff -----------------*/

    private void drawLines(double x, double y, double z, double scale, List<Text> lines) {
        double offset = lines.size() * 0.25 * scale;

        for (Text t: lines) {
            WorldRenderUtils.drawText(t, x, y + offset, z, scale, true);
            offset -= 0.25 * scale;
        }
    }

    private void drawItems(double x, double y, double z, double scale, List<ItemStack> items) {
        double lscale = scale * 0.4;

        for (int i = 0; i < items.size(); i++) {
            drawItem(x, y, z, i + 0.5 - items.size() / 2d, 0, lscale, items.get(i));
        }
    }

    private void drawItem(double x, double y, double z, double offX, double offY, double scale, ItemStack item) {
        if (item.isEmpty())
            return;

        WorldRenderUtils.drawGuiItem(x, y, z, offX * scale, offY * scale, scale, item);

        double w = mc.textRenderer.getWidth("x" + item.getCount()) / 52d;
        WorldRenderUtils.drawText(Text.literal("x" + item.getCount()),
                x, y, z, (offX - w) * scale, (offY - 0.07) * scale, scale * 1.75, false);

        int c = 0;
        for (Map.Entry<Enchantment, Integer> m : EnchantmentHelper.get(item).entrySet()) {
            String text = I18n.translate(m.getKey().getName(2).getString());

            if (text.isEmpty()) continue;

            text = text.replaceFirst("Curse of (.)", "C$1");

            String subText = text.substring(0, Math.min(text.length(), 2)) + m.getValue();

            WorldRenderUtils.drawText(Text.literal(subText).styled(s -> s.withColor(TextColor.fromRgb(m.getKey().isCursed() ? 0xff5050 : 0xffb0e0))),
                    x, y, z, (offX + 0.02) * scale, (offY + 0.75 - c * 0.34) * scale, scale * 1.4, false);
            c--;
        }
    }

    private List<ItemStack> getMainEquipment(Entity e) {
        List<ItemStack> list = Lists.newArrayList(e.getItemsEquipped());
        list.add(list.remove(1));
        return list;
    }

    private void addUUIDFuture(UUID uuid) {
        uuidFutures.put(uuid, uuidExecutor.submit(() -> {
            try {
                String url = "https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names";
                String response = Resources.toString(new URL(url), StandardCharsets.UTF_8);
                ZLogger.info(response);

                JsonElement json = JsonParser.parseString(response);

                if (!json.isJsonArray()) {
                    ZLogger.warn("Invalid response from Mojang API: " + response);
                    return "\u00a7c[Invalid]";
                }

                JsonArray ja = json.getAsJsonArray();

                return ja.get(ja.size() - 1).getAsJsonObject().get("name").getAsString();
            } catch (IOException e) {
                ZLogger.warn("Failed to get name from Mojang API " + e.getMessage());
                return "\u00a7c[Error]";
            }
        }));
    }

}
