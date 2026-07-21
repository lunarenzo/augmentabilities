package com.lunatech.augmentabilities.augment.impl;

import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentTier;
import com.lunatech.augmentabilities.config.AugmentsConfig;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;
import java.util.List;

public class CelestialAegisAugment implements Augment {
    private final AugmentsConfig.CelestialAegisConfig config;

    public CelestialAegisAugment(AugmentsConfig.CelestialAegisConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "CELESTIAL_AEGIS";
    }

    @Override
    public String getName() {
        return config.name;
    }

    @Override
    public List<String> getDescription() {
        return config.description;
    }

    @Override
    public AugmentTier getTier() {
        try {
            return AugmentTier.valueOf(config.tier.toUpperCase());
        } catch (Exception e) {
            return AugmentTier.PRISMATIC;
        }
    }

    @Override
    public boolean isEnabled() {
        return config.enabled;
    }

    @Override
    public void onGeneralDamageTaken(Player victim, EntityDamageEvent event, PlayerAugmentProfile profile) {
        double remainingHealth = victim.getHealth() - event.getFinalDamage();
        if (remainingHealth <= 0.0 && !profile.isOnCooldown(getId())) {
            event.setCancelled(true);
            victim.setHealth(config.reviveHealth);
            victim.setNoDamageTicks(config.invulnerabilityDurationTicks);

            Location loc = victim.getLocation();
            for (Entity entity : victim.getWorld().getNearbyEntities(loc, 4.0, 4.0, 4.0)) {
                if (entity instanceof LivingEntity target && target != victim) {
                    Vector push = target.getLocation().toVector().subtract(loc.toVector()).setY(0.4);
                    if (push.lengthSquared() > 0) {
                        push.normalize();
                    }
                    target.setVelocity(push.multiply(1.2));
                }
            }

            victim.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, loc.clone().add(0, 1, 0), 40, 0.5, 0.5, 0.5, 0.2);
            profile.setCooldown(getId(), config.cooldownMs);
            if (config.activationMessage != null && !config.activationMessage.isEmpty()) {
                victim.sendMessage(ColorParser.of(config.activationMessage).build());
            }
        }
    }
}
