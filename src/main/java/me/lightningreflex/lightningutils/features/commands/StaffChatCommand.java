package me.lightningreflex.lightningutils.features.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import me.lightningreflex.lightningutils.LightningUtils;
import me.lightningreflex.lightningutils.utils.Utils;
import me.lightningreflex.lightningutils.configurations.impl.LangConfig;
import me.lightningreflex.lightningutils.configurations.impl.MainConfig;
import me.lightningreflex.lightningutils.managers.PlayerCache;

public class StaffChatCommand {
    static LangConfig.Commands.StaffChat langStaff = LightningUtils.getLangConfig().getCommands().getStaffchat();
    static MainConfig.StaffChat configStaffChat = LightningUtils.getMainConfig().getStaffchat();
    static MainConfig.Commands commands = LightningUtils.getMainConfig().getCommands();

    public BrigadierCommand createBrigadierCommand(String command) {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
            .<CommandSource>literal(command)
            .requires(ctx -> Utils.hasPermission(ctx, commands.getStaffchat().getPermission()))
            .then(
                BrigadierCommand.requiredArgumentBuilder(langStaff.getArguments().getMessage(), StringArgumentType.greedyString())
                .executes(this::execute)
                .build()
            )
            .executes(this::execute)
            .build();

        // BrigadierCommand implements Command
        return new BrigadierCommand(node);
    }

    private int execute(CommandContext<CommandSource> context) {
        String[] args = context.getInput().split(" ");
        Player player = (Player) context.getSource();
//        String message = StringArgumentType.getString(context, langStaff.getArguments().getMessage());
        String message = context.getInput().substring(context.getInput().indexOf(" ") + 1);

        if (args.length > 1) {
            sendStaffMessage(player, message);
            return 1;
        }

        // Togglable
        if (configStaffChat.isAllow_toggle()) {
            if (PlayerCache.getToggledStaffChat().contains(player.getUniqueId())) {
                PlayerCache.getToggledStaffChat().remove(player.getUniqueId());
                player.sendMessage(Utils.formatString(langStaff.getDisabled()));
            } else {
                PlayerCache.getToggledStaffChat().add(player.getUniqueId());
                player.sendMessage(Utils.formatString(langStaff.getEnabled()));
            }
            return 1;
        }

        // toggling is disabled
        player.sendMessage(Utils.formatString(langStaff.getToggle_disabled()));

        return 1; // indicates success
    }

    public static void sendStaffMessage(Player player, String message) {
        for (Player staff : LightningUtils.getProxy().getAllPlayers()) {
            if (Utils.hasPermission(staff, commands.getStaffchat().getPermission())) {
//                staff.sendMessage(Utils.formatString(langStaff.getMessage(), message));
                // Filter minimessage by removing < and > cause I'm lazy and yeah
                String formattedMessage = langStaff.getMessage()
                    .replace("{server}", player.getCurrentServer().get().getServerInfo().getName())
                    .replace("{player}", player.getUsername())
                    .replace("{message}", message.replaceAll("<", "").replaceAll(">", ""));
                staff.sendMessage(Utils.formatString(formattedMessage));
            }
        }
    }
}
