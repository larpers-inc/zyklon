package dev.vili.zyklon;

import dev.vili.zyklon.command.CommandManager;
import dev.vili.zyklon.eventbus.EventBus;
import dev.vili.zyklon.module.ModuleManager;
import dev.vili.zyklon.setting.ConfigManager;
import dev.vili.zyklon.setting.FriendManager;
import dev.vili.zyklon.setting.XRayManager;
import dev.vili.zyklon.util.ZLogger;
import dev.vili.zyklon.setting.SettingManager;

import net.fabricmc.api.ModInitializer;

import net.minecraft.client.MinecraftClient;

public final class Zyklon implements ModInitializer {
    public static Zyklon INSTANCE;
    public static String name = "Zyklon";
    public static final String version = "0.3.4";
    public static final MinecraftClient mc = MinecraftClient.getInstance();

    public Zyklon() {
        INSTANCE = this;
    }

    public final EventBus EVENT_BUS = new EventBus();
    public ModuleManager moduleManager;
    public SettingManager settingManager;
    public CommandManager commandManager;
    public ConfigManager configManager;
    public FriendManager friendManager;
    public XRayManager xrayManager;


    @Override
    public void onInitialize() {
        long startTime = System.currentTimeMillis();
        ZLogger.info("""

    ╭━━━━╮╱╱╱╭╮╱╭╮
    ╰━━╮━┃╱╱╱┃┃╱┃┃
    ╱╱╭╯╭╋╮╱╭┫┃╭┫┃╭━━┳━╮
    ╱╭╯╭╯┃┃╱┃┃╰╯┫┃┃╭╮┃╭╮╮
    ╭╯━╰━┫╰━╯┃╭╮┫╰┫╰╯┃┃┃┃
    ╰━━━━┻━╮╭┻╯╰┻━┻━━┻╯╰╯
    ╱╱╱╱╱╭━╯┃
    ╱╱╱╱╱╰━━╯

        ~ written by Vili.
        """);

        settingManager = new SettingManager();
        ZLogger.logger.info("setting system initialized.");

        configManager = new ConfigManager();
        ZLogger.logger.info("config manager initialized.");

        friendManager = new FriendManager();
        ZLogger.logger.info("friend manager initialized.");

        moduleManager = new ModuleManager();
        ZLogger.logger.info("module system initialized.");

        xrayManager = new XRayManager();
        ZLogger.logger.info("xray manager initialized.");

        commandManager = new CommandManager();
        ZLogger.logger.info("command system initialized.");

        long finishTime = System.currentTimeMillis() - startTime;
        ZLogger.logger.info("ratted in " + finishTime + "ms.");
    }

    public void postInit() {
        long startTime = System.currentTimeMillis();
        ZLogger.logger.info("phase 2 of zyklon.");

        configManager.load();
        ZLogger.logger.info("configs loaded.");

        friendManager.load();
        ZLogger.logger.info("friends loaded.");

        xrayManager.load();
        ZLogger.logger.info("xray loaded.");

        long finishTime = System.currentTimeMillis() - startTime;
        ZLogger.logger.info("phase 2 of zyklon initialized in " + finishTime + "ms.");
    }
}
