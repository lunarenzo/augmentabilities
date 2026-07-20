package com.lunatech.augmentabilities.config;

import com.lunatech.augmentabilities.config.exception.ConfigValidationException;
import com.lunatech.augmentabilities.config.migration.Migration;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.interfaces.meta.Exclude;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.List;
import java.util.Map;

@ConfigSerializable
public class AugmentsConfig implements VersionedConfig {
    @Comment("Do not change this value!")
    public int configVersion = 1;

    @Override
    @Exclude
    public int configVersion() {
        return configVersion;
    }

    @Override
    @Exclude
    public @NotNull Map<Integer, Migration> migrations() {
        return Map.of();
    }

    @Override
    @Exclude
    public void validate() throws ConfigValidationException {
    }

    @Comment("Vampiric Strike Augment Settings")
    public VampiricStrikeConfig vampiricStrike = new VampiricStrikeConfig();

    @Comment("Last Stand Augment Settings")
    public LastStandConfig lastStand = new LastStandConfig();

    @Comment("Tailwind Augment Settings")
    public TailwindConfig tailwind = new TailwindConfig();

    @Comment("Kinetic Redirection Augment Settings")
    public KineticRedirectionConfig kineticRedirection = new KineticRedirectionConfig();

    @Comment("Chemtech Gas Cloud Augment Settings")
    public ChemtechGasCloudConfig chemtechGasCloud = new ChemtechGasCloudConfig();

    @Comment("Counter-Step Augment Settings")
    public CounterStepConfig counterStep = new CounterStepConfig();

    @Comment("Hextech Overdrive Augment Settings")
    public HextechOverdriveConfig hextechOverdrive = new HextechOverdriveConfig();

    @Comment("Phase Rift Augment Settings")
    public PhaseRiftConfig phaseRift = new PhaseRiftConfig();

    @ConfigSerializable
    public static class VampiricStrikeConfig {
        public String name = "Vampiric Strike";
        public String tier = "COMMON";
        public List<String> description = List.of(
            "<gray>Passive:</gray>",
            "<green>Critical strikes</green> heal you for",
            "<green>15%</green> of the damage dealt."
        );
        public double healPercentage = 0.15;
    }

    @ConfigSerializable
    public static class LastStandConfig {
        public String name = "Last Stand";
        public String tier = "COMMON";
        public List<String> description = List.of(
            "<gray>Passive Cooldown:</gray>",
            "Falling below 3 hearts grants",
            "<amber>Resistance II</amber> for <green>3s</green>.",
            "<dark_gray>Cooldown: 60s</dark_gray>"
        );
        public double triggerHealthThreshold = 6.0;
        public int resistanceDurationTicks = 60;
        public int resistanceAmplifier = 1;
        public long cooldownMs = 60000;
        public String activationMessage = "<red>Last Stand activated!</red>";
    }

    @ConfigSerializable
    public static class TailwindConfig {
        public String name = "Tailwind";
        public String tier = "COMMON";
        public List<String> description = List.of(
            "<gray>Sneak Active:</gray>",
            "Sneaking grants <aqua>Speed I</aqua> for <green>4s</green>.",
            "<dark_gray>Cooldown: 15s</dark_gray>"
        );
        public int speedDurationTicks = 80;
        public int speedAmplifier = 0;
        public long cooldownMs = 15000;
    }

    @ConfigSerializable
    public static class KineticRedirectionConfig {
        public String name = "Kinetic Redirection";
        public String tier = "RARE";
        public List<String> description = List.of(
            "<gray>Passive:</gray>",
            "Receiving <light_purple>projectile damage</light_purple>",
            "stores charges. Your next melee attack",
            "deals <green>+1.5 damage</green> per charge (max 3)."
        );
        public double bonusDamagePerStack = 1.5;
    }

    @ConfigSerializable
    public static class ChemtechGasCloudConfig {
        public String name = "Chemtech Gas Cloud";
        public String tier = "RARE";
        public List<String> description = List.of(
            "<gray>Active:</gray>",
            "Right-click sword to drop a",
            "<dark_green>Poison Cloud for 3s</dark_green>.",
            "<dark_gray>Cooldown: 30s</dark_gray>"
        );
        public int poisonDurationTicks = 40;
        public int poisonAmplifier = 0;
        public int cloudDurationTicks = 60;
        public float cloudRadius = 2.5f;
        public int durabilityCost = 5;
        public long cooldownMs = 30000;
        public String activationMessage = "<green>Chemtech Gas Cloud deployed!</green>";
    }

    @ConfigSerializable
    public static class CounterStepConfig {
        public String name = "Counter-Step";
        public String tier = "RARE";
        public List<String> description = List.of(
            "<gray>Passive Cooldown:</gray>",
            "Successfully <blue>blocking</blue> an attack gives",
            "the attacker <light_purple>Levitation II</light_purple> for 0.75s.",
            "<dark_gray>Cooldown: 15s</dark_gray>"
        );
        public int levitationDurationTicks = 15;
        public int levitationAmplifier = 1;
        public long cooldownMs = 15000;
    }

    @ConfigSerializable
    public static class HextechOverdriveConfig {
        public String name = "Hextech Overdrive";
        public String tier = "PRISMATIC";
        public List<String> description = List.of(
            "<gray>Passive (Limit 1 Prismatic):</gray>",
            "5 consecutive hits deal",
            "<gold>+4 True Damage & Lightning</gold>."
        );
        public int requiredHits = 5;
        public double bonusTrueDamage = 4.0;
        public String activationMessage = "<yellow>Hextech Overdrive triggered!</yellow>";
    }

    @ConfigSerializable
    public static class PhaseRiftConfig {
        public String name = "Phase Rift";
        public String tier = "PRISMATIC";
        public List<String> description = List.of(
            "<gray>Sneak-Sprint (Limit 1 Prismatic):</gray>",
            "Sneaking while sprinting <light_purple>teleports</light_purple>",
            "you 5 blocks forward through spaces.",
            "<dark_gray>Cooldown: 20s</dark_gray>"
        );
        public double teleportDistance = 5.0;
        public long cooldownMs = 20000;
    }
}
