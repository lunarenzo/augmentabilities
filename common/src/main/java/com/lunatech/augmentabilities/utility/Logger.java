package com.lunatech.augmentabilities.utility;


import com.lunatech.augmentabilities.AbstractAugmentAbilities;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

/**
 * A class that provides shorthand access to {@link AbstractAugmentAbilities#getComponentLogger}.
 */
public class Logger {
    /**
     * Get component logger. Shorthand for:
     *
     * @return the component logger {@link AbstractAugmentAbilities#getComponentLogger}.
     */
    @NotNull
    public static ComponentLogger get() {
        return AbstractAugmentAbilities.getInstance().getComponentLogger();
    }
}
