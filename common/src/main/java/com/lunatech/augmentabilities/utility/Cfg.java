package com.lunatech.augmentabilities.utility;

import com.lunatech.augmentabilities.AbstractAugmentAbilities;
import com.lunatech.augmentabilities.config.ConfigHandler;
import com.lunatech.augmentabilities.config.PluginConfig;
import org.jetbrains.annotations.NotNull;

/**
 * Convenience class for accessing {@link ConfigHandler#getConfig}
 */
public final class Cfg {
    /**
     * Convenience method for {@link ConfigHandler#getConfig} to getConnection {@link PluginConfig}
     *
     * @return the config
     */
    @NotNull
    public static PluginConfig get() {
        return AbstractAugmentAbilities.getInstance().getConfigHandler().getConfig();
    }
}
