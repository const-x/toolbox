package idv.const_x.swing;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;


public class AppAppearance {

    private static volatile Image appIcon;

    private AppAppearance() {
    }


    public static void configure(String appName,String iconResource) {
        System.setProperty("app.name", appName);
        System.setProperty("apple.awt.application.name", appName);
        /**
         * macOS 在应用程序启动时，会立即读取 com.apple.mrj.application.apple.menu.about.name 这个属性来决定程序坞（Dock）中显示的名称
         * 在这里设置已经晚了 需要在启动 JVM 时，通过命令行参数来设置系统属性，确保它在任何代码执行前就生效
         * java  -Dcom.apple.mrj.application.apple.menu.about.name="{appName}" -jar YourApp.jar
         */
        //System.setProperty("com.apple.mrj.application.apple.menu.about.name", appName);
        Image icon = loadAppIcon(iconResource);
        if (icon != null) applyDockIcon(icon);
    }

    public static void applyWindowIcon(Window window) {
        if (appIcon != null) window.setIconImage(appIcon);
    }

    private static Image loadAppIcon(String iconResource) {
        if (appIcon != null) return appIcon;
        synchronized (AppAppearance.class) {
            if (appIcon != null) return appIcon;
            try (InputStream inputStream = AppAppearance.class.getResourceAsStream(iconResource)) {
                if (inputStream == null) return null;
                appIcon = ImageIO.read(inputStream);
            } catch (IOException ignored) {
                appIcon = null;
            }
            return appIcon;
        }
    }

    private static void applyDockIcon(Image icon) {
        if (!isMac()) return;
        try {
            Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
            Method getApplication = applicationClass.getMethod("getApplication");
            Object application = getApplication.invoke(null);
            Method setDockIconImage = applicationClass.getMethod("setDockIconImage", Image.class);
            setDockIconImage.invoke(application, icon);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    private static boolean isMac() {
        String osName = System.getProperty("os.name");
        return osName != null && osName.toLowerCase().contains("mac");
    }
}
