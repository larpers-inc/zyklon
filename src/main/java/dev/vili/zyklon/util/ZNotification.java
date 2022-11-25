package dev.vili.zyklon.util;

import dev.vili.zyklon.Zyklon;

import java.awt.*;

public final class ZNotification {
    private static final SystemTray tray = SystemTray.getSystemTray();
    private static final Image image = Toolkit.getDefaultToolkit().createImage(Zyklon.class.getResource("/assets/zyklon/elements/tray/zyklon_32x32.png"));
    private static TrayIcon icon;

    public static void sendNotification(String message, TrayIcon.MessageType type) {
        if (!tray.isSupported()) {
            ZLogger.logger.error("System notifications are not supported on your computer.");
        } else {
            icon = new TrayIcon(image, Zyklon.name);
            icon.setImageAutoSize(true);
            try {
                tray.add(icon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }

        icon.displayMessage(Zyklon.name, message, type);
    }
}
