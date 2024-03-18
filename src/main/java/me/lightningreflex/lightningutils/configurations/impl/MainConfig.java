package me.lightningreflex.lightningutils.configurations.impl;

import lombok.Getter;
import me.lightningreflex.lightningutils.LightningUtils;
import me.lightningreflex.lightningutils.Utils;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Getter
public class MainConfig {
    //------------------------------------------------
    public Commands commands;
    @Getter
    public static class Commands {
        public Send send;
        @Getter
        public static class Send {
            public boolean enabled;
            public String permission;
            public List<String> aliases;
        }
        public Alert alert;
        @Getter
        public static class Alert {
            public boolean enabled;
            public String permission;
            public List<String> aliases;
        }
        public Lobby lobby;
        @Getter
        public static class Lobby {
            public boolean enabled;
            public String permission;
            public List<String> aliases;
        }
        public StaffChat staffchat;
        @Getter
        public static class StaffChat {
            public boolean enabled;
            public String permission;
            public List<String> aliases;
        }
        public Sudo sudo;
        @Getter
        public static class Sudo {
            public boolean enabled;
            public String permission;
            public List<String> aliases;
        }
        public Find find;
        @Getter
        public static class Find {
            public boolean enabled;
            public String permission;
            public List<String> aliases;
        }
        public Ip ip;
        @Getter
        public static class Ip {
            public boolean enabled;
            public String permission;
            public List<String> aliases;
        }
    }

    public Fallback fallback;
    @Getter
    public static class Fallback {
        public boolean enabled;
        public String default_server;
        public Map<String, Server> servers;
        @Getter
        public static class Server {
            public boolean kick;
            public String fallback;
        }
    }

    public Lobby lobby;
    @Getter
    public static class Lobby {
        public String order;
        public List<String> valid_lobbies;
    }

    public StaffChat staffchat;
    @Getter
    public static class StaffChat {
        public boolean enabled;
        public boolean allow_toggle;
        public boolean allow_prefix;
        public String prefix;
    }

    public Sudo sudo;
    @Getter
    public static class Sudo {
        public boolean notify;
    }

    public Clearchat clearchat;
    @Getter
    public static class Clearchat {
        public boolean enabled;
        public boolean network_join;
    }

    // config-version
    public double config_version;
    //------------------------------------------------

    public MainConfig load(final String path) {
        // load from file datadirectory/path, if it doesn't exist, copy from resources path
        Path dataPath = LightningUtils.getDataDirectory();
        Path configPath = dataPath.resolve(path);
        if (!configPath.toFile().exists()) {
            // Ensure folders exist
            dataPath.toFile().mkdirs();
            // Copy from resources
            Utils.ExportResource(path, configPath.toString().substring(configPath.toString().indexOf("/") + 1));
        }
        // for development always export
//        dataPath.toFile().mkdirs();
//        Utils.ExportResource(path, configPath.toString().substring(configPath.toString().indexOf("/") + 1));

        // Load from file
        try {
            InputStream inputStream = new FileInputStream(configPath.toFile());
    //            yaml.load(inputStream);
            MainConfig config = new Yaml(new Constructor(MainConfig.class, new LoaderOptions())).load(inputStream);
            // validate
            MainConfig validatedConfig = config.validate(path, config);
            return validatedConfig;
        } catch (Exception e) {
            LightningUtils.getLogger().error("Failed to load config file " + path);
            e.printStackTrace();
        }
        return null;
    }

    public MainConfig validate(final String path, MainConfig currentConfig) {
        // move config.yml to config-version.yml and create new config.yml
        if (currentConfig.getConfig_version() != 1.1) {
            // move
            Path dataPath = LightningUtils.getDataDirectory();
            Path configPath = dataPath.resolve(path);
            // remove end from . and add -version.yml
            String oldExtension = configPath.toString().substring(configPath.toString().lastIndexOf("."));
            String newExtension = "-" + currentConfig.getConfig_version() + ".yml";
            Path oldConfigPath = dataPath.resolve(path.replace(oldExtension, newExtension));
            // error
            LightningUtils.getLogger().error("Invalid yml version, moving previous yml file to " + oldConfigPath.getFileName() + " and creating new " + configPath.getFileName());
            // use correct names
            // move
            configPath.toFile().renameTo(oldConfigPath.toFile());
            // create new
            return load(path);
        }
        return currentConfig;
    }
}
