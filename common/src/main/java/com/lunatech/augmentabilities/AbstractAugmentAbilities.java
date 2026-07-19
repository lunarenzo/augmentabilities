package com.lunatech.augmentabilities;

import com.lunatech.augmentabilities.config.ConfigHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractAugmentAbilities extends JavaPlugin {
    private static AbstractAugmentAbilities instance;

    /**
     * Gets plugin instance.
     *
     * @return the plugin instance
     */
    public static AbstractAugmentAbilities getInstance() {
        return AbstractAugmentAbilities.instance;
    }

    AbstractAugmentAbilities() {
        AbstractAugmentAbilities.instance = this;
    }

    /**
     * Gets config handler.
     *
     * @return the config handler
     */
    public abstract @NotNull ConfigHandler getConfigHandler();
}
