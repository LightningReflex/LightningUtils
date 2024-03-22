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

import java.util.Optional;

public class IpCommand {
    LangConfig.Commands.Ip langIp = LightningUtils.getLangConfig().getCommands().getIp();
    MainConfig.Commands commands = LightningUtils.getMainConfig().getCommands();

    public BrigadierCommand createBrigadierCommand(String command) {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
            .<CommandSource>literal(command)
            .requires(ctx -> Utils.hasPermission(ctx, commands.getIp().getPermission()))
            .then(
                BrigadierCommand.requiredArgumentBuilder(langIp.getArguments().getPlayer(), StringArgumentType.word())
                    .suggests((ctx, builder) -> {
                        String argument = ctx.getArguments().containsKey(langIp.getArguments().getPlayer())
                            ? StringArgumentType.getString(ctx, langIp.getArguments().getPlayer())
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
        String playerName = StringArgumentType.getString(context, langIp.getArguments().getPlayer());
        Optional<Player> optionalPlayer = LightningUtils.getProxy().getPlayer(playerName);
        if (optionalPlayer.isEmpty()) {
            context.getSource().sendMessage(Utils.formatString(langIp.getPlayer_not_found(), playerName));
            return 1;
        }
        Player argumentPlayer = optionalPlayer.get();
        context.getSource().sendMessage(Utils.formatString(langIp.getSuccess(), playerName, argumentPlayer.getRemoteAddress().getAddress().getHostAddress()));
        return 1; // indicates success
    }

    private int executeError(CommandContext<CommandSource> context) {
        context.getSource().sendMessage(Utils.formatString(langIp.getArguments().getInvalid_syntax()));
        return 1; // indicates success
    }
}
