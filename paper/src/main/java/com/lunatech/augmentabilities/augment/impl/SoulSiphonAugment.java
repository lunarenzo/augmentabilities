package com.lunatech.augmentabilities.augment.impl;

import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentTier;
import com.lunatech.augmentabilities.config.AugmentsConfig;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.List;

public class SoulSiphonAugment implements Augment {
    private final AugmentsConfig.SoulSiphonConfig config;

    public SoulSiphonAugment(AugmentsConfig.SoulSiphonConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "SOUL_SIPHON";
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
    public void onKill(Player killer, LivingEntity victim, PlayerAugmentProfile profile) {
        killer.setFoodLevel(Math.min(20, killer.getFoodLevel() + config.foodRestored));
        killer.setSaturation(Math.min(20.0f, killer.getSaturation() + (float) config.saturationRestored));
        killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, config.regenDurationTicks, config.regenAmplifier));
        killer.getWorld().spawnParticle(Particle.SOUL, killer.getLocation().add(0, 1, 0), 12, 0.3, 0.4, 0.3, 0.05);
    }
}
