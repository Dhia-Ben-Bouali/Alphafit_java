package services;


import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.net.MalformedURLException;

public class NotificationUtil {

    public static void showNotification(String title, String message) throws AWTException, MalformedURLException {
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage("logo.png");
        TrayIcon trayIcon = new TrayIcon(image, "Java AWT Tray Demo");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("System tray icon demo");
        tray.add(trayIcon);

        trayIcon.displayMessage(title, message, MessageType.INFO);
    }
}