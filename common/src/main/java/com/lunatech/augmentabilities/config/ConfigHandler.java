package com.lunatech.augmentabilities.config;

import com.lunatech.augmentabilities.AbstractAugmentAbilities;
import com.lunatech.augmentabilities.Reloadable;
import com.lunatech.augmentabilities.config.loading.ConfigLoader;
import com.lunatech.augmentabilities.config.typeserializer.StringListSerializer;
import com.lunatech.augmentabilities.config.typeserializer.StringObjectMapSerializer;
import org.slf4j.Logger;

import java.nio.file.Path;

/**
 * A class that generates/loads {@literal &} provides access to a configuration file.
 */
public class ConfigHandler implements Reloadable {
    private final AbstractAugmentAbilities plugin;
    private final Path configDir;
    private final Logger logger;

    private PluginConfig cfg;
    private AugmentsConfig augmentsCfg;
    private DatabaseConfig databaseCfg;

    /**
     * Instantiates a new Config handler.
     *
     * @param plugin the plugin instance
     */
    public ConfigHandler(AbstractAugmentAbilities plugin) {
        this.plugin = plugin;
        this.configDir = plugin.getDataFolder().toPath();
        this.logger = plugin.getComponentLogger();
    }

    public ConfigHandler(AbstractAugmentAbilities plugin, Path configDir, Logger logger) {
        this.plugin = plugin;
        this.configDir = configDir;
        this.logger = logger;
    }

    @Override
    public void onLoad(AbstractAugmentAbilities plugin) {
        cfg = new ConfigLoader()
            .withLogger(logger)
            .withDirectory()
            .withPath(configDir.resolve("config.yml"))
            .withHeader("")
            .build(PluginConfig.class);

        augmentsCfg = new ConfigLoader()
            .withLogger(logger)
            .withDirectory()
            .withPath(configDir.resolve("augments.yml"))
            .withHeader("")
            .withSerializer(b -> {
                b.registerExact(StringListSerializer.TYPE_TOKEN, StringListSerializer.INSTANCE);
            })
            .build(AugmentsConfig.class);

        databaseCfg = new ConfigLoader()
            .withLogger(logger)
            .withDirectory()
            .withPath(configDir.resolve("database.yml"))
            .withHeader("")
            .withSerializer(b -> {
                b.registerExact(StringListSerializer.TYPE_TOKEN, StringListSerializer.INSTANCE)
                    .registerExact(StringObjectMapSerializer.TYPE_TOKEN, StringObjectMapSerializer.INSTANCE);
            })
            .build(DatabaseConfig.class);
    }

    /**
     * Gets main config object.
     *
     * @return the config object
     */
    public PluginConfig getConfig() {
        return cfg;
    }

    /**
     * Gets augments config object.
     *
     * @return the config object
     */
    public AugmentsConfig getAugmentsConfig() {
        return augmentsCfg;
    }

    /**
     * Gets database config object.
     *
     * @return the config object
     */
    public DatabaseConfig getDatabaseConfig() {
        return databaseCfg;
    }
}
