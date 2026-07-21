package com.lunatech.augmentabilities.augment.impl;

import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentTier;
import com.lunatech.augmentabilities.config.AugmentsConfig;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.List;

public class AdrenalineRushAugment implements Augment {
    private final AugmentsConfig.AdrenalineRushConfig config;

    public AdrenalineRushAugment(AugmentsConfig.AdrenalineRushConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "ADRENALINE_RUSH";
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
        double maxHealth = victim.getAttribute(Attribute.MAX_HEALTH).getValue();
        double remainingHealth = victim.getHealth() - event.getFinalDamage();
        if (remainingHealth <= maxHealth * config.healthThresholdPercent && !profile.isOnCooldown(getId())) {
            victim.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, config.speedDurationTicks, config.speedAmplifier));
            victim.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, config.hasteDurationTicks, config.hasteAmplifier));
            victim.getWorld().spawnParticle(Particle.FLAME, victim.getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0.05);
            profile.setCooldown(getId(), config.cooldownMs);
            if (config.activationMessage != null && !config.activationMessage.isEmpty()) {
                victim.sendMessage(ColorParser.of(config.activationMessage).build());
            }
        }
    }
}
