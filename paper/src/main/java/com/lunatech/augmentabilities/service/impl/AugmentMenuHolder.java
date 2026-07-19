package com.lunatech.augmentabilities.service.impl;

import com.lunatech.augmentabilities.augment.Augment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class AugmentMenuHolder implements InventoryHolder {
    private final String type;
    private final Inventory inventory;
    private final List<Augment> choices;

    public AugmentMenuHolder(String type, Inventory inventory, List<Augment> choices) {
        this.type = type;
        this.inventory = inventory;
        this.choices = choices;
    }

    public String getType() {
        return type;
    }

    public List<Augment> getChoices() {
        return choices;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
