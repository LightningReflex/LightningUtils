package me.lightningreflex.lightningutils.configurations;

import me.lightningreflex.lightningutils.LightningUtils;
import me.lightningreflex.lightningutils.Utils;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;

public class Config {
//    private Yaml yaml = null;
//
//
//
//    public Object load(final String path) {
//        // load from file datadirectory/path, if it doesn't exist, copy from resources path
//        Path dataPath = LightningUtils.getDataDirectory();
//        Path configPath = dataPath.resolve(path);
////        if (!configPath.toFile().exists()) {
////            // Ensure folders exist
////            dataPath.toFile().mkdirs();
////            // Copy from resources
////            Utils.ExportResource(path, configPath.toString().substring(configPath.toString().indexOf("/") + 1));
////        }
//        // for development always export
//        dataPath.toFile().mkdirs();
//        Utils.ExportResource(path, configPath.toString().substring(configPath.toString().indexOf("/") + 1));
//
//        // Load from file
//        try {
//            InputStream inputStream = new FileInputStream(configPath.toFile());
////            yaml.load(inputStream);
//            yaml = new Yaml(new Constructor(super.getClass(), new LoaderOptions()));
//            return yaml.load(inputStream);
//        } catch (Exception e) {
//            LightningUtils.getLogger().error("Failed to load config file " + path);
//            e.printStackTrace();
//        }
//        return null;
//    }

    // genuinely give up
}
