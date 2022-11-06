package dev.vili.zyklon.command.commands;

import dev.vili.zyklon.Zyklon;
import dev.vili.zyklon.command.Command;
import dev.vili.zyklon.event.events.PacketEvent;
import dev.vili.zyklon.util.ZLogger;
import net.minecraft.SharedConstants;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;
import dev.vili.zyklon.eventbus.Subscribe;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/* @Author BleachDev (https://github.com/BleachDev) */
public class ServerCmd extends Command {

    public ServerCmd() {
        super("server", "Tells information about the server.", "server <address/brand/day/difficulty/ip/motd/ping/permissions/plugins/protocol/version>", "server");
    }

    @Override
    public void onCommand(String[] args, String command) {
        boolean sp = mc.isIntegratedServerRunning();

        if (!sp && mc.getCurrentServerEntry() == null) {
            ZLogger.error("Unable to get server info.");
            return;
        }

        ZLogger.info("Server Info");

        if (args.length == 0) {
            ZLogger.noPrefix(createText("Address", getAddress(sp)));
            ZLogger.noPrefix(createText("Brand", getBrand(sp)));
            ZLogger.noPrefix(createText("Day", getDay(sp)));
            ZLogger.noPrefix(createText("Difficulty", getDifficulty(sp)));
            ZLogger.noPrefix(createText("IP", getIP(sp)));
            ZLogger.noPrefix(createText("Motd", getMotd(sp)));
            ZLogger.noPrefix(createText("Ping", getPing(sp)));
            ZLogger.noPrefix(createText("Permission Level", getPerms(sp)));
            ZLogger.noPrefix(createText("Protocol", getProtocol(sp)));
            ZLogger.noPrefix(createText("Version", getVersion(sp)));
            checkForPlugins();
        } else if (args[0].equalsIgnoreCase("address")) {
            ZLogger.noPrefix(createText("Address", getAddress(sp)));
        } else if (args[0].equalsIgnoreCase("brand")) {
            ZLogger.noPrefix(createText("Brand", getBrand(sp)));
        } else if (args[0].equalsIgnoreCase("day")) {
            ZLogger.noPrefix(createText("Day", getDay(sp)));
        } else if (args[0].equalsIgnoreCase("difficulty")) {
            ZLogger.noPrefix(createText("Difficulty", getDifficulty(sp)));
        } else if (args[0].equalsIgnoreCase("ip")) {
            ZLogger.noPrefix(createText("IP", getIP(sp)));
        } else if (args[0].equalsIgnoreCase("motd")) {
            ZLogger.noPrefix(createText("Motd", getMotd(sp)));
        } else if (args[0].equalsIgnoreCase("ping")) {
            ZLogger.noPrefix(createText("Ping", getPing(sp)));
        } else if (args[0].equalsIgnoreCase("permissions")) {
            ZLogger.noPrefix(createText("Permission Level", getPerms(sp)));
        } else if (args[0].equalsIgnoreCase("plugins")) {
            checkForPlugins();
        } else if (args[0].equalsIgnoreCase("protocol")) {
            ZLogger.noPrefix(createText("Protocol", getProtocol(sp)));
        } else if (args[0].equalsIgnoreCase("version")) {
            ZLogger.noPrefix(createText("Version", getVersion(sp)));
        } else {
            ZLogger.error("Unknown argument: " + args[0]);
        }
    }

    @Subscribe
    public void onPacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof CommandSuggestionsS2CPacket) {
            Zyklon.INSTANCE.EVENT_BUS.unregister(this);

            CommandSuggestionsS2CPacket packet = (CommandSuggestionsS2CPacket) event.getPacket();
            List<String> plugins = packet.getSuggestions().getList().stream()
                    .map(s -> {
                        String[] split = s.getText().split(":");
                        return split.length != 1 ? split[0].replace("/", "") : null;
                    })
                    .filter(Objects::nonNull)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

            if (!plugins.isEmpty()) {
                ZLogger.noPrefix(createText("Plugins \u00a7f(" + plugins.size() + ")", "\u00a7a" + String.join("\u00a7f, \u00a7a", plugins)));
            } else {
                ZLogger.noPrefix("\u00a7cNo plugins found");
            }
        }
    }

    public Text createText(String name, String value) {
        boolean newlines = value.contains("\n");
        return Text.literal("\u00a77" + name + "\u00a7f:" + (newlines ? "\n" : " " ) + "\u00a7a" + value).styled(style -> style
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to copy to clipboard")))
                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, Formatting.strip(value))));
    }

    public void checkForPlugins() {
        Zyklon.INSTANCE.EVENT_BUS.register(this); // Plugins
        mc.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(0, "/"));

        Thread timeoutThread = new Thread(() -> {
            try {
                Thread.sleep(5000);
                ZLogger.noPrefix("\u00a7cPlugin check timed out");
            } catch (InterruptedException ignored) {}
        });
        timeoutThread.setDaemon(true);
        timeoutThread.start();
    }

    public String getAddress(boolean singleplayer) {
        if (singleplayer)
            return "Singleplayer";

        return mc.getCurrentServerEntry().address != null ? mc.getCurrentServerEntry().address : "Unknown";
    }

    public String getBrand(boolean singleplayer) {
        if (singleplayer)
            return "Integrated Server";

        return mc.player.getServerBrand() != null ? mc.player.getServerBrand() : "Unknown";
    }

    public String getDay(boolean singleplayer) {
        return "Day " + (mc.world.getTimeOfDay() / 24000L);
    }

    public String getDifficulty(boolean singleplayer) {
        return StringUtils.capitalize(mc.world.getDifficulty().getName()) + " (Local: " + mc.world.getLocalDifficulty(mc.player.getBlockPos()).getLocalDifficulty() + ")";
    }

    public String getIP(boolean singleplayer) {
        try {
            if (singleplayer)
                return InetAddress.getLocalHost().getHostAddress();

            return mc.getCurrentServerEntry().address != null ? InetAddress.getByName(mc.getCurrentServerEntry().address).getHostAddress() : "Unknown";
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }

    public String getMotd(boolean singleplayer) {
        if (singleplayer)
            return "-";

        return mc.getCurrentServerEntry().label != null ? mc.getCurrentServerEntry().label.getString() : "Unknown";
    }

    public String getPing(boolean singleplayer) {
        PlayerListEntry playerEntry = mc.player.networkHandler.getPlayerListEntry(mc.player.getGameProfile().getId());
        return playerEntry == null ? "0" : Integer.toString(playerEntry.getLatency());
    }

    public String getPerms(boolean singleplayer) {
        int p = 0;
        while (mc.player.hasPermissionLevel(p + 1) && p < 5) p++;

        return switch (p) {
            case 0 -> "0 (No Perms)";
            case 1 -> "1 (No Perms)";
            case 2 -> "2 (Player Command Access)";
            case 3 -> "3 (Server Command Access)";
            case 4 -> "4 (Operator)";
            default -> p + " (Unknown)";
        };
    }

    public String getProtocol(boolean singleplayer) {
        if (singleplayer)
            return Integer.toString(SharedConstants.getProtocolVersion());

        return Integer.toString(mc.getCurrentServerEntry().protocolVersion);
    }

    public String getVersion(boolean singleplayer) {
        if (singleplayer)
            return SharedConstants.getGameVersion().getName();

        return mc.getCurrentServerEntry().version != null ? mc.getCurrentServerEntry().version.getString() : "Unknown (" + SharedConstants.getGameVersion().getName() + ")";
    }
}

