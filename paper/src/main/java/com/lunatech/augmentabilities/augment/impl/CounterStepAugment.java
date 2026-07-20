package com.lunatech.augmentabilities.augment.impl;

import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentTier;
import com.lunatech.augmentabilities.config.AugmentsConfig;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.List;

public class CounterStepAugment implements Augment {
    private final AugmentsConfig.CounterStepConfig config;

    public CounterStepAugment(AugmentsConfig.CounterStepConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "COUNTER_STEP";
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
            return AugmentTier.RARE;
        }
    }

    @Override
    public boolean isEnabled() {
        return config.enabled;
    }

    @Override
    public void onDamageTaken(Player victim, Entity attacker, EntityDamageByEntityEvent event, PlayerAugmentProfile profile) {
        if (victim.isBlocking() && attacker instanceof LivingEntity livingAttacker && !profile.isOnCooldown(getId())) {
            livingAttacker.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, config.levitationDurationTicks, config.levitationAmplifier));
            victim.getWorld().spawnParticle(Particle.CLOUD, victim.getLocation().add(0, 1, 0), 5, 0.2, 0.2, 0.2, 0.1);
            profile.setCooldown(getId(), config.cooldownMs);
        }
    }
}
