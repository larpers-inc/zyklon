package me.vp.zyklon;

import me.vp.zyklon.command.CommandManager;
import me.vp.zyklon.eventbus.EventBus;
import me.vp.zyklon.module.ModuleManager;
import me.vp.zyklon.setting.ConfigManager;
import me.vp.zyklon.setting.FriendManager;
import me.vp.zyklon.setting.SettingManager;
import me.vp.zyklon.setting.XRayManager;
import me.vp.zyklon.util.ZLogger;

import net.fabricmc.api.ModInitializer;

import net.minecraft.client.MinecraftClient;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Zyklon implements ModInitializer {
    public static Zyklon INSTANCE;
    public static String name = "Zyklon";
    public static final String version = "0.02b";
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

        ~ written by Vp.
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

    private static String getDay() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

}
