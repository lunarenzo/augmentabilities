package com.lunatech.augmentabilities.augment;

public enum AugmentTier {
    COMMON("Common", "<green>Common</green>"),
    RARE("Rare", "<blue>Rare</blue>"),
    PRISMATIC("Prismatic", "<light_purple>Prismatic</light_purple>");

    private final String name;
    private final String coloredName;

    AugmentTier(String name, String coloredName) {
        this.name = name;
        this.coloredName = coloredName;
    }

    public String getName() {
        return name;
    }

    public String getColoredName() {
        return coloredName;
    }
}
