package me.lightningreflex.lightningutils.managers;

import com.velocitypowered.api.command.BrigadierCommand;
import me.lightningreflex.lightningutils.LightningUtils;
import me.lightningreflex.lightningutils.configurations.impl.MainConfig;
import me.lightningreflex.lightningutils.features.commands.*;

import java.util.List;

public class CommandManager {
    public static void registerCommands() {
        // registerCommand(new CommandClass(), "commandName", "alias1", "alias2");
        MainConfig mainConfig = LightningUtils.getMainConfig();
        MainConfig.Commands commands = mainConfig.getCommands();
        if (commands.getSend().isEnabled())
            registerCommand(new SendCommand().createBrigadierCommand(commands.getSend().getAliases().get(0)), commands.getSend().getAliases());
        if (commands.getAlert().isEnabled())
            registerCommand(new AlertCommand().createBrigadierCommand(commands.getAlert().getAliases().get(0)), commands.getAlert().getAliases());
        if (commands.getLobby().isEnabled())
            registerCommand(new LobbyCommand().createBrigadierCommand(commands.getLobby().getAliases().get(0)), commands.getLobby().getAliases());
        if (commands.getStaffchat().isEnabled()) {
            if (mainConfig.getStaffchat().isEnabled()) {
                registerCommand(new StaffChatCommand().createBrigadierCommand(commands.getStaffchat().getAliases().get(0)), commands.getStaffchat().getAliases());
            } else {
                LightningUtils.getLogger().warn("The staffchat command is enabled but the feature is not enabled in the config. Command will not be registered.");
            }
        }
    }

    private static void registerCommand(BrigadierCommand commandClass,  List<String> aliases) {
        String[] aliasesArray = aliases.stream().filter(alias -> !alias.equals(aliases.get(0))).toArray(String[]::new);
        com.velocitypowered.api.command.CommandManager commandManager = LightningUtils.getProxy().getCommandManager();
        commandManager.register(
            commandManager.metaBuilder(commandClass)
                .aliases(aliasesArray)
                .build(),
            commandClass
        );
    }
}
