package me.lightningreflex.lightningutils.features.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.VelocityServer;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.network.ConnectionManager;
import me.lightningreflex.lightningutils.LightningUtils;
import me.lightningreflex.lightningutils.Utils;
import me.lightningreflex.lightningutils.configurations.impl.LangConfig;
import me.lightningreflex.lightningutils.configurations.impl.MainConfig;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.Optional;

public class SudoCommand {
    LangConfig.Commands.Sudo langSudo = LightningUtils.getLangConfig().getCommands().getSudo();
    MainConfig.Commands commands = LightningUtils.getMainConfig().getCommands();

    public BrigadierCommand createBrigadierCommand(String command) {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
            .<CommandSource>literal(command)
            .requires(ctx -> Utils.hasPermission(ctx, commands.getSudo().getPermission()))
            .then(
                BrigadierCommand.requiredArgumentBuilder(langSudo.getArguments().getPlayer(), StringArgumentType.word())
                    .suggests((ctx, builder) -> {
                        String argument = ctx.getArguments().containsKey(langSudo.getArguments().getPlayer())
                            ? StringArgumentType.getString(ctx, langSudo.getArguments().getPlayer())
                            : "";
                        LightningUtils.getProxy().getAllPlayers().stream().filter((player) -> player.getUsername().regionMatches(true, 0, argument, 0, argument.length())).forEach(player -> builder.suggest(player.getUsername()));
                        return builder.buildFuture();
                    })
                    .then(
                        BrigadierCommand.requiredArgumentBuilder(langSudo.getArguments().getText(), StringArgumentType.greedyString())
                        .executes(this::execute)
                        .build()
                    )
                .executes(this::executeError)
                .build()
            )
            .executes(this::executeError)
            .build();

        // BrigadierCommand implements Command
        return new BrigadierCommand(node);
    }

    private int execute(CommandContext<CommandSource> context) {
        String playerName = StringArgumentType.getString(context, langSudo.getArguments().getPlayer());
        Optional<Player> optionalPlayer = LightningUtils.getProxy().getPlayer(playerName);
        if (optionalPlayer.isEmpty()) {
            context.getSource().sendMessage(Utils.formatString(langSudo.getPlayer_not_found(), playerName));
            return 1;
        }
        Player victim = optionalPlayer.get();
        String text = StringArgumentType.getString(context, langSudo.getArguments().getText());

        // cool reflection was useless, spent hours cooking, just to end up cooking water, and ending up with this
        ConnectedPlayer connectedVictim = (ConnectedPlayer) victim;
        // force a slash cause like, non-commands will cause a signature error
        if (!text.startsWith("/")) text = "/" + text;
        connectedVictim.spoofChatInput(text);
        context.getSource().sendMessage(Utils.formatString(langSudo.getSuccess(), playerName, text));
        return 1; // indicates success
    }

    private int executeError(CommandContext<CommandSource> context) {
        context.getSource().sendMessage(Utils.formatString(langSudo.getArguments().getInvalid_syntax()));
        return 1; // indicates success
    }
}
