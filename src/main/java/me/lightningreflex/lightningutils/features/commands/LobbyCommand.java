package me.lightningreflex.lightningutils.features.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.lightningreflex.lightningutils.LightningUtils;
import me.lightningreflex.lightningutils.utils.Utils;
import me.lightningreflex.lightningutils.configurations.impl.LangConfig;
import me.lightningreflex.lightningutils.configurations.impl.MainConfig;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class LobbyCommand {
    LangConfig.Commands.Lobby langLobby = LightningUtils.getLangConfig().getCommands().getLobby();
    MainConfig mainConfig = LightningUtils.getMainConfig();
    MainConfig.Commands commands = mainConfig.getCommands();

    public BrigadierCommand createBrigadierCommand(String command) {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
            .<CommandSource>literal(command)
            .requires(ctx -> Utils.hasPermission(ctx, commands.getLobby().getPermission()))
            .then(
                BrigadierCommand.requiredArgumentBuilder(langLobby.getArguments().getServer(), StringArgumentType.word())
                    .suggests((ctx, builder) -> {
                        LightningUtils.getProxy().getAllServers().stream()
                            .filter(server -> mainConfig.getLobby().getValid_lobbies().stream().anyMatch(server.getServerInfo().getName()::matches))
                            .filter(server -> !server.equals(((Player) ctx.getSource()).getCurrentServer().get().getServer()))
                            .filter(server -> {
                                String argument = ctx.getArguments().containsKey(langLobby.getArguments().getServer())
                                    ? StringArgumentType.getString(ctx, langLobby.getArguments().getServer())
                                    : "";
                                return server.getServerInfo().getName().regionMatches(true, 0, argument, 0, argument.length());
                            })
                            .map(server -> server.getServerInfo().getName())
                            .forEach(builder::suggest);
                        return builder.buildFuture();
                    })
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
        String order = mainConfig.getLobby().getOrder();
        List<String> validLobbiesRegex = mainConfig.getLobby().getValid_lobbies();

        List<RegisteredServer> validLobbies = new java.util.ArrayList<>(LightningUtils.getProxy().getAllServers().stream()
            .filter(server1 -> validLobbiesRegex.stream().anyMatch(server1.getServerInfo().getName()::matches))
            .toList());

        // Check if they are already in a lobby, if so remove the lobby from the list
        RegisteredServer currentServer = ((Player) context.getSource()).getCurrentServer().get().getServer();
        // player is in a lobby
        if (validLobbies.stream().anyMatch(currentServer::equals)) {
            validLobbies.remove(currentServer);
            // if validLobbies is empty, it means they are on the only lobby, tell them they are already in a lobby
            if (validLobbies.isEmpty()) {
                context.getSource().sendMessage(Utils.formatString(langLobby.getAlready_in_lobby(), currentServer.getServerInfo().getName()));
                return 1;
            }
        }

        // if server is set, check if server exists and if lobby, then send player to that server
        if (args.length > 1) {
            String server = args[1];
            Optional<RegisteredServer> serverArgument = LightningUtils.getProxy().getServer(server);
            if (serverArgument.isEmpty()) {
                context.getSource().sendMessage(Utils.formatString(langLobby.getServer_does_not_exist(), server));
                return 1;
            }
            // Server exists, check if it's a valid lobby
            if (validLobbies.stream().noneMatch(serverArgument.get()::equals)) {
                context.getSource().sendMessage(Utils.formatString(langLobby.getIs_not_lobby(), server));
                return 1;
            }
            // Server is a valid lobby, send player to that server
            Player player = (Player) context.getSource();
            player.createConnectionRequest(serverArgument.get()).fireAndForget();
            player.sendMessage(Utils.formatString(langLobby.getSuccess(), server));
        }

//        # Order to send players in.
//        # random: Send players to a random lobby.
//        # priority: Send players to the lobby with the least players.
//        #
//        # Valid lobbies are checked with regex.
//        order: 'priority'
//        valid_lobbies:
//            - 'lobby-[0-9]+'
        // check if order is valid, otherwise log error and priority default
        if (!order.equals("random") && !order.equals("priority")) {
            LightningUtils.getLogger().error("Invalid order in config, defaulting to priority.");
            order = "priority";
        }
        if (order.equals("random")) {
            // send player to random lobby
            Player player = (Player) context.getSource();
            RegisteredServer randomLobby = validLobbies.get(Utils.getRandomInt(0, validLobbies.size() - 1));
            player.createConnectionRequest(randomLobby).fireAndForget();
            player.sendMessage(Utils.formatString(langLobby.getSuccess(), randomLobby.getServerInfo().getName()));
        } else if (order.equals("priority")) {
            // send player to lobby with the least players
            Player player = (Player) context.getSource();
            RegisteredServer leastPlayersLobby = validLobbies.stream()
                .min(Comparator.comparingInt(server2 -> server2.getPlayersConnected().size()))
                .orElseThrow();
            player.createConnectionRequest(leastPlayersLobby).fireAndForget();
            player.sendMessage(Utils.formatString(langLobby.getSuccess(), leastPlayersLobby.getServerInfo().getName()));
        }
        return 1; // indicates success
    }
}
