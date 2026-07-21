package com.lunatech.augmentabilities.augment.impl;

import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentTier;
import com.lunatech.augmentabilities.config.AugmentsConfig;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.List;

public class FrostbiteStrikeAugment implements Augment {
    private final AugmentsConfig.FrostbiteStrikeConfig config;

    public FrostbiteStrikeAugment(AugmentsConfig.FrostbiteStrikeConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "FROSTBITE_STRIKE";
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
    public void onAttack(Player attacker, LivingEntity victim, EntityDamageByEntityEvent event, PlayerAugmentProfile profile) {
        int hits = profile.incrementFrostbiteHits(victim.getUniqueId());
        if (hits >= config.requiredHits && !profile.isOnCooldown(getId())) {
            profile.resetFrostbiteHits(victim.getUniqueId());
            victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, config.slownessDurationTicks, config.slownessAmplifier));
            victim.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, config.slownessDurationTicks, 0));
            victim.getWorld().spawnParticle(Particle.SNOWFLAKE, victim.getLocation().add(0, 1, 0), 20, 0.4, 0.5, 0.4, 0.05);
            profile.setCooldown(getId(), config.cooldownMs);
        }
    }
}
