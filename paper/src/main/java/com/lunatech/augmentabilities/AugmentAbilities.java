package com.lunatech.augmentabilities;

import com.lunatech.augmentabilities.api.AugmentAbilitiesAPI;
import com.lunatech.augmentabilities.command.CommandHandler;
import com.lunatech.augmentabilities.config.ConfigHandler;
import com.lunatech.augmentabilities.cooldown.CooldownHandler;
import com.lunatech.augmentabilities.database.handler.DatabaseHandler;
import com.lunatech.augmentabilities.hook.HookManager;
import com.lunatech.augmentabilities.listener.ListenerHandler;
import com.lunatech.augmentabilities.messaging.MessagingHandler;
import com.lunatech.augmentabilities.threadutil.SchedulerHandler;
import com.lunatech.augmentabilities.translation.TranslationHandler;
import com.lunatech.augmentabilities.updatechecker.UpdateHandler;
import com.lunatech.augmentabilities.utility.DB;
import com.lunatech.augmentabilities.utility.Logger;
import com.lunatech.augmentabilities.utility.Messaging;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Main class.
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class AugmentAbilities extends AbstractAugmentAbilities {
    private static AugmentAbilities instance;

    // Handlers/Managers
    private ConfigHandler configHandler;
    private TranslationHandler translationHandler;
    private DatabaseHandler databaseHandler;
    private MessagingHandler messagingHandler;
    private HookManager hookManager;
    private CommandHandler commandHandler;
    private ListenerHandler listenerHandler;
    private UpdateHandler updateHandler;
    private SchedulerHandler schedulerHandler;
    private CooldownHandler cooldownHandler;
    private AugmentAbilitiesAPIProvider apiHandler;
    private com.lunatech.augmentabilities.service.impl.DefaultAugmentService augmentService;

    // Handlers list (defines order of load/enable/disable)
    private List<? extends Reloadable> handlers;

    @Override
    public void onLoad() {
        instance = this;

        configHandler = new ConfigHandler(this);
        translationHandler = new TranslationHandler(configHandler);
        databaseHandler = DatabaseHandler.builder()
            .withConfigHandler(configHandler)
            .withLogger(getComponentLogger())
            .withMigrate(true)
            .build();
        messagingHandler = MessagingHandler.builder()
            .withLogger(getComponentLogger())
            .withName(getName())
            .build();
        hookManager = new HookManager(this);
        commandHandler = new CommandHandler(this);
        listenerHandler = new ListenerHandler(this);
        updateHandler = new UpdateHandler(this);
        schedulerHandler = new SchedulerHandler();
        cooldownHandler = new CooldownHandler();
        apiHandler = new AugmentAbilitiesAPIProvider(this);
        augmentService = new com.lunatech.augmentabilities.service.impl.DefaultAugmentService(this);

        handlers = List.of(
            configHandler,
            translationHandler,
            databaseHandler,
            messagingHandler,
            hookManager,
            commandHandler,
            listenerHandler,
            updateHandler,
            schedulerHandler,
            cooldownHandler,
            apiHandler,
            augmentService
        );

        DB.init(databaseHandler);
        Messaging.init(messagingHandler);
        for (Reloadable handler : handlers)
            handler.onLoad(instance);

        com.lunatech.augmentabilities.augment.AugmentRegistry.init(configHandler.getAugmentsConfig());
    }

    @Override
    public void onEnable() {
        for (Reloadable handler : handlers)
            handler.onEnable(instance);

        if (!DB.isStarted()) {
            Logger.get().warn(ColorParser.of("<yellow>Database handler failed to start. Database support has been disabled.").build());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        if (!Messaging.isReady() && configHandler.getDatabaseConfig().messaging.enabled) {
            Logger.get().warn(ColorParser.of("<yellow>Messaging handler failed to start. Messaging support has been disabled.").build());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                com.lunatech.augmentabilities.profile.PlayerAugmentProfile profile = augmentService.getProfile(p.getUniqueId());
                if (profile.hasAugment("CHRONOS_ANCHOR")) {
                    profile.recordPosition(p.getLocation());
                }
            }
        }, 20L, 20L);
    }

    @Override
    public void onDisable() {
        for (Reloadable handler : handlers.reversed()) // If reverse doesn't work implement a new List with your desired disable order
            handler.onDisable(instance);
    }

    /**
     * Safely reloads configuration files, translations, and augment settings.
     */
    public void onReload() {
        configHandler.onLoad(this);
        translationHandler.onEnable(this);
        com.lunatech.augmentabilities.augment.AugmentRegistry.init(configHandler.getAugmentsConfig());
    }

    @Override
    public @NotNull ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public @NotNull HookManager getHookManager() {
        return hookManager;
    }

    public @NotNull UpdateHandler getUpdateHandler() {
        return updateHandler;
    }

    public @NotNull AugmentAbilitiesAPI getApiHandler() {
        return apiHandler;
    }

    public @NotNull com.lunatech.augmentabilities.service.impl.DefaultAugmentService getAugmentService() {
        return augmentService;
    }
}
