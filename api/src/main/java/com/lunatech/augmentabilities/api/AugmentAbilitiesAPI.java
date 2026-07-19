package com.lunatech.augmentabilities.api;

import org.jetbrains.annotations.ApiStatus;

/**
 * The AugmentAbilitiesAPI class is the main entry point for accessing the AugmentAbilities API.
 */
public abstract class AugmentAbilitiesAPI {
    private static AugmentAbilitiesAPI INSTANCE;

    /**
     * Gets the instance of the AugmentAbilitiesAPI.
     *
     * @return the instance of AugmentAbilitiesAPI
     * @since 1.0.0
     */
    public static AugmentAbilitiesAPI getInstance() {
        if (INSTANCE == null)
            throw new RuntimeException("API was accessed before being initialized!");
        return INSTANCE;
    }

    /**
     * Sets the instance of the AugmentAbilitiesAPI.
     * This method is intended for internal use by the api provider only.
     *
     * @param api the instance of AugmentAbilitiesAPI to set
     * @since 1.0.0
     */
    @ApiStatus.Internal
    protected static void setInstance(AugmentAbilitiesAPI api) {
        INSTANCE = api;
    }
}
