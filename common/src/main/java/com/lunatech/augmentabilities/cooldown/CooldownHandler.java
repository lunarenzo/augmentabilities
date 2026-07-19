package com.lunatech.augmentabilities.cooldown;

import com.lunatech.augmentabilities.AbstractAugmentAbilities;
import com.lunatech.augmentabilities.Reloadable;
import com.lunatech.augmentabilities.cooldown.listener.ListenerHandler;
import com.lunatech.augmentabilities.database.Queries;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class CooldownHandler implements Reloadable {
    private ListenerHandler listenerHandler;
    private ScheduledTask autoSaveTask;

    @Override
    public void onLoad(AbstractAugmentAbilities plugin) {
        if (listenerHandler != null)
            return;

        listenerHandler = new ListenerHandler(plugin);
        listenerHandler.onLoad(plugin);
    }

    @Override
    public void onEnable(AbstractAugmentAbilities plugin) {
        if (listenerHandler == null)
            return;

        listenerHandler.onEnable(plugin);
        autoSaveTask = plugin.getServer().getAsyncScheduler().runAtFixedRate(plugin, autoSaveTask(plugin), 10L, 10L, TimeUnit.MINUTES);
    }

    @Override
    public void onDisable(AbstractAugmentAbilities plugin) {
        if (listenerHandler == null)
            return;

        autoSaveTask.cancel();
        listenerHandler.onDisable(plugin);
        Cooldowns.reset();
    }

    private Consumer<ScheduledTask> autoSaveTask(JavaPlugin plugin) {
        return task -> {
            for (final Player p : plugin.getServer().getOnlinePlayers()) {
                if (!p.isOnline())
                    continue;

                Queries.Cooldown.save(p);
            }
        };
    }
}
