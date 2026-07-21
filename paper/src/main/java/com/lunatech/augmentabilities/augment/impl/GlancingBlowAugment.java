package com.lunatech.augmentabilities.augment.impl;

import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentTier;
import com.lunatech.augmentabilities.config.AugmentsConfig;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GlancingBlowAugment implements Augment {
    private final AugmentsConfig.GlancingBlowConfig config;

    public GlancingBlowAugment(AugmentsConfig.GlancingBlowConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "GLANCING_BLOW";
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
    public void onDamageTaken(Player victim, Entity attacker, EntityDamageByEntityEvent event, PlayerAugmentProfile profile) {
        if (ThreadLocalRandom.current().nextDouble() < config.dodgeChance) {
            double damage = event.getDamage();
            event.setDamage(damage * (1.0 - config.damageReductionPercent));
            victim.getWorld().spawnParticle(Particle.SMOKE, victim.getLocation().add(0, 1, 0), 8, 0.2, 0.3, 0.2, 0.02);
        }
    }
}
