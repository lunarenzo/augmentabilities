package com.lunatech.augmentabilities.augment.impl;

import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentTier;
import com.lunatech.augmentabilities.config.AugmentsConfig;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.List;

public class TailwindAugment implements Augment {
    private final AugmentsConfig.TailwindConfig config;

    public TailwindAugment(AugmentsConfig.TailwindConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "TAILWIND";
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
    public void onSneak(Player player, boolean isSneaking, PlayerAugmentProfile profile) {
        if (isSneaking && !profile.isOnCooldown(getId())) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, config.speedDurationTicks, config.speedAmplifier));
            player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 15, 0.3, 0.1, 0.3, 0.02);
            profile.setCooldown(getId(), config.cooldownMs);
        }
    }
}
