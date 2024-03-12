package me.lightningreflex.lightningutils.features.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.lightningreflex.lightningutils.LightningUtils;
import me.lightningreflex.lightningutils.configurations.impl.MainConfig;
import net.kyori.adventure.text.Component;

import java.util.Optional;

public class FallbackListener {

    // fallback to the lobby, don't ever kick from the network unless specified
    @Subscribe(order = PostOrder.LAST)
    public void onKickedFromServer(KickedFromServerEvent event) {
        MainConfig.Fallback fallbackConfig = LightningUtils.getMainConfig().getFallback();
        // already checked in the manager
//        if (!fallbackConfig.isEnabled()) {
//            return;
//        }98

        if (event.kickedDuringServerConnect()) { // connected from another server
            return;
        }

        String serverName = event.getServer().getServerInfo().getName();
        Optional<RegisteredServer> defaultServer = LightningUtils.getProxy().getServer(fallbackConfig.getDefault_server());

        MainConfig.Fallback.Server serverConfig = fallbackConfig.getServers().get(serverName);
        if (serverConfig == null) {
            if (defaultServer.isEmpty()) {
                LightningUtils.getLogger().error("The default server is not set or does not exist.");
                return;
            }
            event.setResult(KickedFromServerEvent.RedirectPlayer.create(defaultServer.get()));
            return;
        }

        if (serverConfig.isKick()) {
            Optional<Component> kickReason = event.getServerKickReason();
            if (kickReason.isPresent()) {
                event.setResult(KickedFromServerEvent.DisconnectPlayer.create(kickReason.get()));
                return;
            }
            event.setResult(KickedFromServerEvent.DisconnectPlayer.create(Component.text("You were kicked from the server.")));
            return;
        }

        Optional<RegisteredServer> fallbackServer = LightningUtils.getProxy().getServer(serverConfig.getFallback());
        if (fallbackServer.isEmpty()) {
            LightningUtils.getLogger().error("The fallback server is not set or does not exist.");
            return;
        }
        event.setResult(KickedFromServerEvent.RedirectPlayer.create(fallbackServer.get()));
    }
}