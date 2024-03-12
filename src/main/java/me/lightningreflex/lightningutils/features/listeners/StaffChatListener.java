package me.lightningreflex.lightningutils.features.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import me.lightningreflex.lightningutils.LightningUtils;
import me.lightningreflex.lightningutils.Utils;
import me.lightningreflex.lightningutils.configurations.impl.LangConfig;
import me.lightningreflex.lightningutils.configurations.impl.MainConfig;
import me.lightningreflex.lightningutils.features.commands.StaffChatCommand;
import me.lightningreflex.lightningutils.managers.PlayerCache;

public class StaffChatListener {

    @Subscribe
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        MainConfig.StaffChat configStaffChat = LightningUtils.getMainConfig().getStaffchat();
        MainConfig.Commands commands = LightningUtils.getMainConfig().getCommands();

        // Check for perms and enabled
        if (
            !configStaffChat.isEnabled() ||
            !Utils.hasPermission(player, commands.getStaffchat().getPermission())
        ) {
            return;
        }

        // Check for allow_prefix and prefix match
        if (
            configStaffChat.isAllow_prefix() &&
            event.getMessage().startsWith(configStaffChat.getPrefix())
        ) {
            event.setResult(PlayerChatEvent.ChatResult.denied());
            String message = event.getMessage().substring(configStaffChat.getPrefix().length());
            StaffChatCommand.sendStaffMessage(player, message);
            return;
        }

        // Check for toggled and if it's allowed
        if (
            PlayerCache.getToggledStaffChat().contains(player.getUniqueId()) &&
            configStaffChat.isAllow_toggle()
        ) {
            event.setResult(PlayerChatEvent.ChatResult.denied());
            StaffChatCommand.sendStaffMessage(player, event.getMessage());
            return;
        }
    }
}
