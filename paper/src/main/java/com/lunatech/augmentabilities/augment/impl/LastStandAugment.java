package com.lunatech.augmentabilities.augment.impl;

import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentTier;
import com.lunatech.augmentabilities.config.AugmentsConfig;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.List;

public class LastStandAugment implements Augment {
    private final AugmentsConfig.LastStandConfig config;

    public LastStandAugment(AugmentsConfig.LastStandConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "LAST_STAND";
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
        double finalHealth = victim.getHealth() - event.getFinalDamage();
        if (finalHealth < config.triggerHealthThreshold && !profile.isOnCooldown(getId())) {
            victim.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, config.resistanceDurationTicks, config.resistanceAmplifier));
            victim.getWorld().spawnParticle(Particle.CRIT, victim.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.05);
            profile.setCooldown(getId(), config.cooldownMs);
            if (config.activationMessage != null && !config.activationMessage.isEmpty()) {
                victim.sendMessage(ColorParser.of(config.activationMessage).build());
            }
        }
    }
}
