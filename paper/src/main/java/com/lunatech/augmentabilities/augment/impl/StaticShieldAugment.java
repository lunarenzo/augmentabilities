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
import org.bukkit.util.Vector;
import java.util.List;

public class StaticShieldAugment implements Augment {
    private final AugmentsConfig.StaticShieldConfig config;

    public StaticShieldAugment(AugmentsConfig.StaticShieldConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "STATIC_SHIELD";
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
    public void onSneak(Player player, boolean isSneaking, PlayerAugmentProfile profile) {
        if (isSneaking && player.isBlocking() && !profile.isOnCooldown(getId())) {
            Location loc = player.getLocation();
            double radius = config.radius;

            for (Entity entity : player.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
                if (entity instanceof LivingEntity target && target != player) {
                    target.damage(config.damage, player);
                    Vector push = target.getLocation().toVector().subtract(loc.toVector()).setY(0.3);
                    if (push.lengthSquared() > 0) {
                        push.normalize();
                    }
                    target.setVelocity(push.multiply(config.knockbackPower));
                }
            }

            player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, loc.clone().add(0, 1, 0), 30, radius / 2, 0.5, radius / 2, 0.1);
            profile.setCooldown(getId(), config.cooldownMs);
            if (config.activationMessage != null && !config.activationMessage.isEmpty()) {
                player.sendMessage(ColorParser.of(config.activationMessage).build());
            }
        }
    }
}
