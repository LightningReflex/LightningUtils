package me.lightningreflex.lightningutils.features.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import me.lightningreflex.lightningutils.LightningUtils;
import me.lightningreflex.lightningutils.configurations.impl.MainConfig;
import net.kyori.adventure.text.Component;

import java.util.Objects;

public class ClearchatListener {

    @Subscribe
    // ServerPostConnect is slow and ServerConnectedEvent doesn't do the initial server >:(
    public void onServerConnected(ServerConnectedEvent event) {
        MainConfig.Clearchat clearchatConfig = LightningUtils.getMainConfig().getClearchat();

        if (!clearchatConfig.isEnabled()) return;
        clearChat(event.getPlayer());
    }

    @Subscribe
    public void onServerPostConnect(ServerPostConnectEvent event) {
        if (event.getPreviousServer() != null) return; // is first join
        MainConfig.Clearchat clearchatConfig = LightningUtils.getMainConfig().getClearchat();

        if (!clearchatConfig.isEnabled() || !clearchatConfig.isNetwork_join()) return;
        clearChat(event.getPlayer());
    }

    public void clearChat(Player player) {
        // 300 line clear cause uh good enough
        Component empty = Component.newline();
        for (int i = 0; i < 300; i++) {
            empty = empty.append(Component.newline());
        }

        player.sendMessage(empty);
    }
}