package com.lunatech.augmentabilities.cooldown.listener;

import com.lunatech.augmentabilities.AbstractAugmentAbilities;
import com.lunatech.augmentabilities.Reloadable;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to handle registration of event listeners.
 */
@SuppressWarnings("FieldCanBeLocal")
public class ListenerHandler implements Reloadable {
    private final AbstractAugmentAbilities plugin;
    private final List<Listener> listeners = new ArrayList<>();

    public ListenerHandler(AbstractAugmentAbilities plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(AbstractAugmentAbilities plugin) {
    }

    @Override
    public void onEnable(AbstractAugmentAbilities plugin) {
        listeners.clear();
        listeners.add(new CooldownListener(plugin));

        for (Listener listener : listeners) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    @Override
    public void onDisable(AbstractAugmentAbilities plugin) {
    }
}
