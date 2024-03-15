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

import java.util.Optional;

public class FindCommand {
    LangConfig.Commands.Find langFind = LightningUtils.getLangConfig().getCommands().getFind();
    MainConfig.Commands commands = LightningUtils.getMainConfig().getCommands();

    public BrigadierCommand createBrigadierCommand(String command) {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
            .<CommandSource>literal(command)
            .requires(ctx -> Utils.hasPermission(ctx, commands.getFind().getPermission()))
            .then(
                BrigadierCommand.requiredArgumentBuilder(langFind.getArguments().getPlayer(), StringArgumentType.word())
                    .suggests((ctx, builder) -> {
                        String argument = ctx.getArguments().containsKey(langFind.getArguments().getPlayer())
                            ? StringArgumentType.getString(ctx, langFind.getArguments().getPlayer())
                            : "";
                        LightningUtils.getProxy().getAllPlayers().stream().filter((player) -> player.getUsername().regionMatches(true, 0, argument, 0, argument.length())).forEach(player -> builder.suggest(player.getUsername()));
                        return builder.buildFuture();
                    })
                .executes(this::execute)
                .build()
            )
            .executes(this::executeError)
            .build();

        // BrigadierCommand implements Command
        return new BrigadierCommand(node);
    }

    private int execute(CommandContext<CommandSource> context) {
        String playerName = StringArgumentType.getString(context, langFind.getArguments().getPlayer());
        Optional<Player> optionalPlayer = LightningUtils.getProxy().getPlayer(playerName);
        if (optionalPlayer.isEmpty()) {
            context.getSource().sendMessage(Utils.formatString(langFind.getPlayer_not_found(), playerName));
            return 1;
        }
        Player argumentPlayer = optionalPlayer.get();
        if (argumentPlayer.getCurrentServer().isPresent()) {
            context.getSource().sendMessage(Utils.formatString(langFind.getSuccess(), playerName, argumentPlayer.getCurrentServer().get().getServerInfo().getName()));
        } else {
            context.getSource().sendMessage(Utils.formatString(langFind.getPlayer_not_found(), playerName));
        }
        return 1; // indicates success
    }

    private int executeError(CommandContext<CommandSource> context) {
        context.getSource().sendMessage(Utils.formatString(langFind.getArguments().getInvalid_syntax()));
        return 1; // indicates success
    }
}
