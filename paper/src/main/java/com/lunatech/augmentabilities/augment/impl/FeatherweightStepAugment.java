package com.lunatech.augmentabilities.augment.impl;

import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentTier;
import com.lunatech.augmentabilities.config.AugmentsConfig;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;
import java.util.List;

public class FeatherweightStepAugment implements Augment {
    private final AugmentsConfig.FeatherweightStepConfig config;

    public FeatherweightStepAugment(AugmentsConfig.FeatherweightStepConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "FEATHERWEIGHT_STEP";
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
            return AugmentTier.COMMON;
        }
    }

    @Override
    public boolean isEnabled() {
        return config.enabled;
    }

    @Override
    public void onGeneralDamageTaken(Player victim, EntityDamageEvent event, PlayerAugmentProfile profile) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            double originalDamage = event.getDamage();
            event.setDamage(originalDamage * (1.0 - config.fallDamageReduction));

            Vector direction = victim.getLocation().getDirection().setY(0.2);
            if (direction.lengthSquared() > 0) {
                direction.normalize();
            }
            victim.setVelocity(direction.multiply(config.forwardImpulse));
            victim.getWorld().spawnParticle(Particle.CLOUD, victim.getLocation(), 10, 0.3, 0.1, 0.3, 0.05);
        }
    }
}
