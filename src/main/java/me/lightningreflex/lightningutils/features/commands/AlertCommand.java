package me.lightningreflex.lightningutils.features.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import me.lightningreflex.lightningutils.LightningUtils;
import me.lightningreflex.lightningutils.Utils;
import me.lightningreflex.lightningutils.configurations.impl.LangConfig;
import me.lightningreflex.lightningutils.configurations.impl.MainConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class AlertCommand {
    LangConfig.Commands.Alert langAlert = LightningUtils.getLangConfig().getCommands().getAlert();
    MainConfig.Commands commands = LightningUtils.getMainConfig().getCommands();

    public BrigadierCommand createBrigadierCommand(String command) {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
            .<CommandSource>literal(command)
            .requires(ctx -> Utils.hasPermission((Player) ctx, commands.getAlert().getPermission()))
            .then(
                BrigadierCommand.requiredArgumentBuilder(langAlert.getArguments().getMessage(), StringArgumentType.greedyString())
                .executes(this::execute)
                .build()
            )
            .executes(this::executeError)
            .build();

        // BrigadierCommand implements Command
        return new BrigadierCommand(node);
    }

    private int execute(CommandContext<CommandSource> context) {
        String message = StringArgumentType.getString(context, "message");
        for (Player player : LightningUtils.getProxy().getAllPlayers()) {
            player.sendMessage(Utils.formatString(langAlert.getMessage(), message));
        }
        return 1; // indicates success
    }

    private int executeError(CommandContext<CommandSource> context) {
        context.getSource().sendMessage(Utils.formatString(langAlert.getArguments().getInvalid_syntax()));
        return 1; // indicates success
    }
}
