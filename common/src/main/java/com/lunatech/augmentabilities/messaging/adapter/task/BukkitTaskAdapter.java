package com.lunatech.augmentabilities.messaging.adapter.task;

import com.lunatech.augmentabilities.AbstractAugmentAbilities;
import org.bukkit.Bukkit;

import java.util.concurrent.TimeUnit;

/**
 * Bukkit platform specific task runner implementation.
 */
public class BukkitTaskAdapter implements TaskAdapter {
    public BukkitTaskAdapter() {
    }

    @Override
    public void init(Runnable runnable, long delay, long interval, TimeUnit timeUnit) {
        Bukkit.getAsyncScheduler().runAtFixedRate(AbstractAugmentAbilities.getInstance(), (task) -> runnable.run(), delay, interval, timeUnit);
    }

    @Override
    public void cancel() {
        Bukkit.getAsyncScheduler().cancelTasks(AbstractAugmentAbilities.getInstance());
    }
}
