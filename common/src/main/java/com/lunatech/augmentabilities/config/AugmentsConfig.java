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

    @Comment("List of entire tiers to disable (e.g., [\"PRISMATIC\"] or [\"RARE\", \"PRISMATIC\"])")
    public List<String> disabledTiers = List.of();

    @Comment("Vampiric Strike Augment Settings")
    public VampiricStrikeConfig vampiricStrike = new VampiricStrikeConfig();

    @Comment("Last Stand Augment Settings")
    public LastStandConfig lastStand = new LastStandConfig();

    @Comment("Tailwind Augment Settings")
    public TailwindConfig tailwind = new TailwindConfig();

    @Comment("Adrenaline Rush Augment Settings")
    public AdrenalineRushConfig adrenalineRush = new AdrenalineRushConfig();

    @Comment("Featherweight Step Augment Settings")
    public FeatherweightStepConfig featherweightStep = new FeatherweightStepConfig();

    @Comment("Glancing Blow Augment Settings")
    public GlancingBlowConfig glancingBlow = new GlancingBlowConfig();

    @Comment("Kinetic Redirection Augment Settings")
    public KineticRedirectionConfig kineticRedirection = new KineticRedirectionConfig();

    @Comment("Chemtech Gas Cloud Augment Settings")
    public ChemtechGasCloudConfig chemtechGasCloud = new ChemtechGasCloudConfig();

    @Comment("Counter-Step Augment Settings")
    public CounterStepConfig counterStep = new CounterStepConfig();

    @Comment("Frostbite Strike Augment Settings")
    public FrostbiteStrikeConfig frostbiteStrike = new FrostbiteStrikeConfig();

    @Comment("Static Shield Augment Settings")
    public StaticShieldConfig staticShield = new StaticShieldConfig();

    @Comment("Soul Siphon Augment Settings")
    public SoulSiphonConfig soulSiphon = new SoulSiphonConfig();

    @Comment("Hextech Overdrive Augment Settings")
    public HextechOverdriveConfig hextechOverdrive = new HextechOverdriveConfig();

    @Comment("Phase Rift Augment Settings")
    public PhaseRiftConfig phaseRift = new PhaseRiftConfig();

    @Comment("Chronos Anchor Augment Settings")
    public ChronosAnchorConfig chronosAnchor = new ChronosAnchorConfig();

    @Comment("Celestial Aegis Augment Settings")
    public CelestialAegisConfig celestialAegis = new CelestialAegisConfig();

    @Comment("Void Singularity Augment Settings")
    public VoidSingularityConfig voidSingularity = new VoidSingularityConfig();

    @ConfigSerializable
    public static class VampiricStrikeConfig {
        public boolean enabled = true;
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
        public boolean enabled = true;
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
        public boolean enabled = true;
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
    public static class AdrenalineRushConfig {
        public boolean enabled = true;
        public String name = "Adrenaline Rush";
        public String tier = "COMMON";
        public List<String> description = List.of(
            "<gray>Passive Cooldown:</gray>",
            "Falling below 50% HP grants",
            "<aqua>Speed I</aqua> & <gold>Haste I</gold> for 4s.",
            "<dark_gray>Cooldown: 25s</dark_gray>"
        );
        public double healthThresholdPercent = 0.50;
        public int speedDurationTicks = 80;
        public int speedAmplifier = 0;
        public int hasteDurationTicks = 80;
        public int hasteAmplifier = 0;
        public long cooldownMs = 25000;
        public String activationMessage = "<gold>Adrenaline Rush triggered!</gold>";
    }

    @ConfigSerializable
    public static class FeatherweightStepConfig {
        public boolean enabled = true;
        public String name = "Featherweight Step";
        public String tier = "COMMON";
        public List<String> description = List.of(
            "<gray>Passive:</gray>",
            "Reduces fall damage by <green>50%</green>",
            "and converts impact into a forward dash."
        );
        public double fallDamageReduction = 0.50;
        public double forwardImpulse = 0.6;
    }

    @ConfigSerializable
    public static class GlancingBlowConfig {
        public boolean enabled = true;
        public String name = "Glancing Blow";
        public String tier = "COMMON";
        public List<String> description = List.of(
            "<gray>Passive:</gray>",
            "<yellow>15% chance</yellow> to dodge",
            "<green>40%</green> of incoming damage."
        );
        public double dodgeChance = 0.15;
        public double damageReductionPercent = 0.40;
    }

    @ConfigSerializable
    public static class KineticRedirectionConfig {
        public boolean enabled = true;
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
        public boolean enabled = true;
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
        public boolean enabled = true;
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
    public static class FrostbiteStrikeConfig {
        public boolean enabled = true;
        public String name = "Frostbite Strike";
        public String tier = "RARE";
        public List<String> description = List.of(
            "<gray>Passive Cooldown:</gray>",
            "3 consecutive hits apply",
            "<blue>Slowness II</blue> for 2.5s.",
            "<dark_gray>Cooldown: 15s</dark_gray>"
        );
        public int requiredHits = 3;
        public int slownessDurationTicks = 50;
        public int slownessAmplifier = 1;
        public long cooldownMs = 15000;
    }

    @ConfigSerializable
    public static class StaticShieldConfig {
        public boolean enabled = true;
        public String name = "Static Shield";
        public String tier = "RARE";
        public List<String> description = List.of(
            "<gray>Sneak + Shield Active:</gray>",
            "Unleashes an electric shockwave",
            "damaging and knocking back enemies.",
            "<dark_gray>Cooldown: 20s</dark_gray>"
        );
        public double radius = 4.0;
        public double damage = 3.0;
        public double knockbackPower = 0.8;
        public long cooldownMs = 20000;
        public String activationMessage = "<aqua>Static Shield discharged!</aqua>";
    }

    @ConfigSerializable
    public static class SoulSiphonConfig {
        public boolean enabled = true;
        public String name = "Soul Siphon";
        public String tier = "RARE";
        public List<String> description = List.of(
            "<gray>Passive On Kill:</gray>",
            "Killing an entity restores food",
            "and grants <green>Regeneration II</green> for 3s."
        );
        public int foodRestored = 4;
        public double saturationRestored = 4.0;
        public int regenDurationTicks = 60;
        public int regenAmplifier = 1;
    }

    @ConfigSerializable
    public static class HextechOverdriveConfig {
        public boolean enabled = true;
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
        public boolean enabled = true;
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

    @ConfigSerializable
    public static class ChronosAnchorConfig {
        public boolean enabled = true;
        public String name = "Chronos Anchor";
        public String tier = "PRISMATIC";
        public List<String> description = List.of(
            "<gray>Sneak + Empty Hand Active (Limit 1):</gray>",
            "Teleports you to position from 4s ago",
            "and heals <green>25% missing HP</green>.",
            "<dark_gray>Cooldown: 45s</dark_gray>"
        );
        public double healMissingHealthPercent = 0.25;
        public long cooldownMs = 45000;
        public String activationMessage = "<light_purple>Chronos Anchor rewinded time!</light_purple>";
    }

    @ConfigSerializable
    public static class CelestialAegisConfig {
        public boolean enabled = true;
        public String name = "Celestial Aegis";
        public String tier = "PRISMATIC";
        public List<String> description = List.of(
            "<gray>Passive Fatal Blow Protection (Limit 1):</gray>",
            "Taking fatal damage cancels death,",
            "restores health & grants invulnerability.",
            "<dark_gray>Cooldown: 180s</dark_gray>"
        );
        public double reviveHealth = 4.0;
        public int invulnerabilityDurationTicks = 40;
        public long cooldownMs = 180000;
        public String activationMessage = "<gold><b>CELESTIAL AEGIS SAVED YOUR LIFE!</b></gold>";
    }

    @ConfigSerializable
    public static class VoidSingularityConfig {
        public boolean enabled = true;
        public String name = "Void Singularity";
        public String tier = "PRISMATIC";
        public List<String> description = List.of(
            "<gray>Sneak + Sword Active (Limit 1):</gray>",
            "Summons a vortex pulling enemies",
            "before detonating for <dark_purple>6 True Damage</dark_purple>.",
            "<dark_gray>Cooldown: 35s</dark_gray>"
        );
        public double castRange = 8.0;
        public double pullRadius = 4.5;
        public double detonationTrueDamage = 6.0;
        public long cooldownMs = 35000;
        public String activationMessage = "<dark_purple>Void Singularity summoned!</dark_purple>";
    }
}
