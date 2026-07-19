package com.lunatech.augmentabilities;

/**
 * Implemented in classes that should support being reloaded IE executing the methods during runtime after startup.
 */
public interface Reloadable {
    /**
     * On plugin load.
     */
    default void onLoad(AbstractAugmentAbilities plugin) {
    }

    /**
     * On plugin enable.
     */
    default void onEnable(AbstractAugmentAbilities plugin) {
    }

    /**
     * On plugin disable.
     */
    default void onDisable(AbstractAugmentAbilities plugin) {
    }

}
