package com.lunatech.augmentabilities.listener;

import com.lunatech.augmentabilities.AbstractAugmentAbilities;
import com.lunatech.augmentabilities.AugmentAbilities;
import com.lunatech.augmentabilities.Reloadable;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to handle registration of event listeners.
 */
public class ListenerHandler implements Reloadable {
    private final AugmentAbilities plugin;
    private final List<Listener> listeners = new ArrayList<>();

    /**
     * Instantiates a the Listener handler.
     *
     * @param plugin the plugin instance
     */
    public ListenerHandler(AugmentAbilities plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEnable(AbstractAugmentAbilities plugin) {
        listeners.clear(); // Clear the list to avoid duplicate listeners when reloading the plugin
        listeners.add(new com.lunatech.augmentabilities.listener.AugmentListener(((AugmentAbilities) plugin).getAugmentService()));

        // Register listeners here
        for (Listener listener : listeners) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }
}
