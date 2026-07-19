package com.lunatech.augmentabilities;

import com.lunatech.augmentabilities.api.AugmentAbilitiesAPI;

class AugmentAbilitiesAPIProvider extends AugmentAbilitiesAPI implements Reloadable {
    private final AugmentAbilities plugin;

    AugmentAbilitiesAPIProvider(AugmentAbilities plugin) {
        super();
        this.plugin = plugin;
        setInstance(this);
    }
}
