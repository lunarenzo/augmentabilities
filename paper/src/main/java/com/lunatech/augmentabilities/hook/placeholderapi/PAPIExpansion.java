package com.lunatech.augmentabilities.hook.placeholderapi;

import com.lunatech.augmentabilities.AugmentAbilities;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A PlaceholderAPI expansion. Read the docs at <a href="https://wiki.placeholderapi.com/developers/creating-a-placeholderexpansion/">here</a> on how to register your custom placeholders.
 */
public class PAPIExpansion extends PlaceholderExpansion {
    private final AugmentAbilities plugin;

    public PAPIExpansion(AugmentAbilities plugin) {
        this.plugin = plugin;
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public @NotNull String getIdentifier() {
        return plugin.getPluginMeta().getName().replace(' ', '_').toLowerCase();
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getPluginMeta().getAuthors());
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // This needs to be true, or PlaceholderAPI will unregister the expansion during a plugin reload.
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer p, @NotNull String params) {
        return switch (params) {
            case "ability" -> "placeholder text";
            case "ability_cooldown" -> "placeholder text2";
            default -> null;
        };
    }
}
