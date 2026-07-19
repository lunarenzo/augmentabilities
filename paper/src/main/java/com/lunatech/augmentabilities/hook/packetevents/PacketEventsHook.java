package com.lunatech.augmentabilities.hook.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.lunatech.augmentabilities.AbstractAugmentAbilities;
import com.lunatech.augmentabilities.AugmentAbilities;
import com.lunatech.augmentabilities.hook.AbstractHook;
import com.lunatech.augmentabilities.hook.Hook;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;

/**
 * A hook that enables API for PacketEvents.
 */
public class PacketEventsHook extends AbstractHook {
    /**
     * Instantiates a new PacketEvents hook.
     *
     * @param plugin the plugin instance
     */
    public PacketEventsHook(AugmentAbilities plugin) {
        super(plugin);
    }

    @Override
    public void onLoad(AbstractAugmentAbilities plugin) {
        if (!isPluginPresent(Hook.PacketEvents.getPluginName()))
            return;

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(getPlugin()));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable(AbstractAugmentAbilities plugin) {
        if (!isPluginEnabled(Hook.PacketEvents.getPluginName()))
            return;

        PacketEvents.getAPI().init();
    }

    @Override
    public void onDisable(AbstractAugmentAbilities plugin) {
        if (!isPluginEnabled(Hook.PacketEvents.getPluginName()))
            return;

        PacketEvents.getAPI().terminate();
    }

    @Override
    public boolean isHookLoaded() {
        return isPluginPresent(Hook.PacketEvents.getPluginName()) && PacketEvents.getAPI().isLoaded();
    }
}
