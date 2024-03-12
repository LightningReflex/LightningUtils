package me.lightningreflex.lightningutils.features.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.lightningreflex.lightningutils.LightningUtils;
import me.lightningreflex.lightningutils.Utils;
import me.lightningreflex.lightningutils.configurations.impl.LangConfig;
import me.lightningreflex.lightningutils.configurations.impl.MainConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SendCommand {
    LangConfig.Commands.Send langSend = LightningUtils.getLangConfig().getCommands().getSend();
    MainConfig.Commands commands = LightningUtils.getMainConfig().getCommands();

    public BrigadierCommand createBrigadierCommand(String command) {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
            .<CommandSource>literal(command)
            .requires(ctx -> Utils.hasPermission((Player) ctx, commands.getSend().getPermission()))
            .then(
                BrigadierCommand.requiredArgumentBuilder(langSend.getArguments().getFrom(), StringArgumentType.word())
                    .suggests((ctx, builder) -> {
                        String argument = ctx.getArguments().containsKey(langSend.getArguments().getFrom())
                            ? StringArgumentType.getString(ctx, langSend.getArguments().getFrom())
                            : "";
                        return getSuggestionsCompletableFuture(builder, argument);
                    })
                    .then(BrigadierCommand.requiredArgumentBuilder(langSend.getArguments().getTo(), StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            String argument = ctx.getArguments().containsKey(langSend.getArguments().getTo())
                                ? StringArgumentType.getString(ctx, langSend.getArguments().getTo())
                                : "";
                            return getSuggestionsCompletableFuture(builder, argument);
                        })
                        .executes(this::execute)
                        .build())
                    .executes(this::executeError)
                    .build()
            )
            .executes(this::executeError)
            .build();

        // BrigadierCommand implements Command
        return new BrigadierCommand(node);
    }

    private CompletableFuture<Suggestions> getSuggestionsCompletableFuture(SuggestionsBuilder builder, String argument) {
        LightningUtils.getProxy().getAllServers().stream().filter((server) -> server.getServerInfo().getName().regionMatches(true, 0, argument, 0, argument.length())).forEach(server -> builder.suggest("+" + server.getServerInfo().getName()));
        LightningUtils.getProxy().getAllPlayers().stream().filter((player) -> player.getUsername().regionMatches(true, 0, argument, 0, argument.length())).forEach(player -> builder.suggest(player.getUsername()));
        return builder.buildFuture();
    }

    private int execute(CommandContext<CommandSource> context) {
        String[] args = context.getInput().split(" ");
        Player player = (Player) context.getSource();

        if (args[1].startsWith("+")) { // Send a server
            Optional<RegisteredServer> sourceServer = LightningUtils.getProxy().getServer(args[1].substring(1));

            if (sourceServer.isPresent()) { // Check if server exists
                if (args[2].startsWith("+")) { // Send to server
                    Optional<RegisteredServer> destServer = LightningUtils.getProxy().getServer(args[2].substring(1));

                    if (destServer.isPresent()) { // Check if server exists
                        // Server to server
                        player.sendMessage(Utils.formatString(langSend.getSuccess_executor(), args[1], args[2]));
                        for (Player p : sourceServer.get().getPlayersConnected()) {
                            p.sendMessage(Utils.formatString(langSend.getWarning_player(), args[2], player.getUsername()));
                            p.createConnectionRequest(destServer.get()).fireAndForget();
                        }

                    } else { // Server is invalid
                        player.sendMessage(Utils.formatString(langSend.getServer_does_not_exist(), args[2]));
                    }


                } else { // Send to player
                    Optional<Player> destPlayer = LightningUtils.getProxy().getPlayer(args[2]);

                    if (destPlayer.isPresent()) { // Check if player exists
                        // Server to player
                        player.sendMessage(Utils.formatString(langSend.getSuccess_executor(), args[1], args[2]));
                        for (Player p : sourceServer.get().getPlayersConnected()) {
                            p.sendMessage(Utils.formatString(langSend.getWarning_player(), args[2], player.getUsername()));
                            p.createConnectionRequest(destPlayer.get().getCurrentServer().get().getServer()).fireAndForget();
                        }

                    } else { // Player is invalid
                        player.sendMessage(Utils.formatString(langSend.getPlayer_offline(), args[2]));
                    }
                }

            } else { // Server is invalid
                player.sendMessage(Utils.formatString(langSend.getServer_does_not_exist(), args[1]));
            }




        } else { // Send a player
            Optional<Player> sourcePlayer = LightningUtils.getProxy().getPlayer(args[1]);

            if (sourcePlayer.isPresent()) { // Check if player exists
                if (args[2].startsWith("+")) { // Send to server
                    Optional<RegisteredServer> destServer = LightningUtils.getProxy().getServer(args[2].substring(1));

                    if (destServer.isPresent()) { // Check if server exists
                        // Player to server
                        player.sendMessage(Utils.formatString(langSend.getSuccess_executor(), args[1], args[2]));
                        sourcePlayer.get().sendMessage(Utils.formatString(langSend.getWarning_player(), args[2], player.getUsername()));
                        sourcePlayer.get().createConnectionRequest(destServer.get()).fireAndForget();

                    } else { // Server is invalid
                        player.sendMessage(Utils.formatString(langSend.getServer_does_not_exist(), args[2]));
                    }


                } else { // Send to player
                    Optional<Player> destPlayer = LightningUtils.getProxy().getPlayer(args[2]);

                    if (destPlayer.isPresent()) { // Check if player exists
                        // Player to player
                        player.sendMessage(Utils.formatString(langSend.getSuccess_executor(), args[1], args[2]));
                        sourcePlayer.get().createConnectionRequest(destPlayer.get().getCurrentServer().get().getServer()).fireAndForget();

                    } else { // Player is invalid
                        player.sendMessage(Utils.formatString(langSend.getPlayer_offline(), args[2]));
                    }
                }

            } else { // Player is invalid
                player.sendMessage(Utils.formatString(langSend.getPlayer_offline(), args[1]));
            }
        }
        return 1; // indicates success
    }

    private int executeError(CommandContext<CommandSource> context) {
        context.getSource().sendMessage(Utils.formatString(langSend.getArguments().getInvalid_syntax()));
        return 1; // indicates success
    }
}
