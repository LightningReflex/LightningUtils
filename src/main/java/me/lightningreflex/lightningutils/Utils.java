package me.lightningreflex.lightningutils;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class Utils {
    static MiniMessage mm = MiniMessage.miniMessage();

    static public boolean ExportResource(String resourceName, String relativeExportLocation) {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        boolean success = false;
        try {
            if (!resourceName.startsWith("/")) {
                resourceName = "/" + resourceName;
            }
            // Note that each / is a directory down in the "jar tree" been the jar the root of the tree
            stream = LightningUtils.class.getResourceAsStream(resourceName);
            if (stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            jarFolder = new File(
                LightningUtils.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath())
                .getParentFile()
                .getPath()
                .replace('\\', '/');
            if (!jarFolder.endsWith("/")) {
                jarFolder += "/";
            }
//            if (!relativeExportLocation.endsWith("/")) {
//                relativeExportLocation += "/";
//            }
//            if (!fileExists(jarFolder + relativeExportLocation)) {
//                makeFolder(jarFolder + relativeExportLocation);
//            }
            if (!new File(jarFolder + relativeExportLocation).getParentFile().exists()) {
                new File(jarFolder + relativeExportLocation).getParentFile().mkdirs();
            }
            resStreamOut = new FileOutputStream(jarFolder + relativeExportLocation);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) stream.close();
                if (resStreamOut != null) resStreamOut.close();
            } catch (Exception ignored) {}
        }

        return success;
    }

    public static boolean hasPermission(Player player, String permission) {
        if (permission == null || permission.isEmpty()) return true;
        return
            player.hasPermission("lightningutils." + permission) ||
            player.hasPermission("lightningutils.*") ||
            player.hasPermission("*");
    }

    // use minimessage .deserialize and String.format
    public static Component formatString(String string, Object... objects) {
        return mm.deserialize(String.format(string, objects));
    }

    //
    // |        |      |
    // |        |      |  Text
    // |        |      |  Text
    // |______  |______|
    //
    // Generate Banner component
    // return list of seperate lines
    public static List<Component> generateBanner(String text1, String text2) {
        return Arrays.asList(
            Component.newline(),
            Component.text(" |        |      |").color(NamedTextColor.GOLD),
            Component.text(" |        |      |  ").color(NamedTextColor.GOLD).append(Component.text(text1).color(NamedTextColor.YELLOW)),
            Component.text(" |        |      |  ").color(NamedTextColor.GOLD).append(Component.text(text2).color(NamedTextColor.YELLOW)),
            Component.text(" |______  |______|").color(NamedTextColor.GOLD),
            Component.newline()
        );
    }

    public static int getRandomInt(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }
}
