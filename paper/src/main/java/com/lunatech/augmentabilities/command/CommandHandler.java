package com.lunatech.augmentabilities.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIPaperConfig;
import com.lunatech.augmentabilities.AbstractAugmentAbilities;
import com.lunatech.augmentabilities.AugmentAbilities;
import com.lunatech.augmentabilities.Reloadable;

/**
 * A class to handle registration of commands.
 */
public class CommandHandler implements Reloadable {
    public static final String BASE_PERM = "augmentabilities.command";
    private final AugmentAbilities plugin;

    /**
     * Instantiates the Command handler.
     *
     * @param plugin the plugin
     */
    public CommandHandler(AugmentAbilities plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(AbstractAugmentAbilities plugin) {
        CommandAPI.onLoad(
            new CommandAPIPaperConfig(plugin)
                .silentLogs(true)
        );
    }

    @Override
    public void onEnable(AbstractAugmentAbilities plugin) {
        if (!CommandAPI.isLoaded())
            return;

        CommandAPI.onEnable();

        // Register commands here
        new AugmentAbilitiesCommand(plugin)
            .command()
            .withAliases()
            .register();
    }

    @Override
    public void onDisable(AbstractAugmentAbilities plugin) {
        if (!CommandAPI.isLoaded())
            return;

        CommandAPI.onDisable();
    }
}