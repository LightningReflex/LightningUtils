package me.lightningreflex.lightningutils;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import me.lightningreflex.lightningutils.configurations.impl.LangConfig;
import me.lightningreflex.lightningutils.configurations.impl.MainConfig;
import me.lightningreflex.lightningutils.managers.CommandManager;
import me.lightningreflex.lightningutils.managers.ListenerManager;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
    id = "lightningutils",
    name = "LightningUtils",
    version = BuildConstants.VERSION,
    description = "The speedy velocity utility plugin",
    authors = {"LightningReflex"}
)
public class LightningUtils {
    @Getter
    private static LightningUtils instance;

    @Getter
    private static Logger logger;
    @Getter
    private static ProxyServer proxy;
    @Getter
    private static @DataDirectory Path dataDirectory;

    @Getter
    private static MainConfig mainConfig;
    @Getter
    private static LangConfig langConfig;

    private final Metrics.Factory metricsFactory;


    @Inject
    public LightningUtils(final ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory, Metrics.Factory metricsFactory) {
        LightningUtils.proxy = proxy;
        LightningUtils.logger = logger;
        LightningUtils.dataDirectory = dataDirectory;
        this.metricsFactory = metricsFactory;
        instance = this;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        long startupTime = System.currentTimeMillis();

        // bstats
        int pluginId = 21360;
        Metrics metrics = metricsFactory.make(this, pluginId);

        // Load config
        langConfig = new LangConfig().load("lang.yml");
        mainConfig = new MainConfig().load("config.yml");

        // Register commands
        CommandManager.registerCommands();
        // Register listeners
        ListenerManager.registerListeners();

//
//        ║        ║      ║
//        ║        ║      ║  LightningUtils - v1.0 (0ms)
//        ║        ║      ║  The speedy velocity utility plugin.
//        ╚══════  ╚══════╝
//

    // Generate Banner component
    // return list of seperate lines
        // Startup done lol
        for (Component component : Utils.generateBanner(
            "LightningUtils" + " - v" + BuildConstants.VERSION + " (" + (System.currentTimeMillis() - startupTime) + "ms)",
            "The speedy velocity utility plugin."
        )) {
            getProxy().getConsoleCommandSource().sendMessage(component);
        }
    }

}
