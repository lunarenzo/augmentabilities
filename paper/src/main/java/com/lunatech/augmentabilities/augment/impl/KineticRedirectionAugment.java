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
import java.util.List;

public class KineticRedirectionAugment implements Augment {
    private final AugmentsConfig.KineticRedirectionConfig config;

    public KineticRedirectionAugment(AugmentsConfig.KineticRedirectionConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "KINETIC_REDIRECTION";
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
    public void onDamageTaken(Player victim, Entity attacker, EntityDamageByEntityEvent event, PlayerAugmentProfile profile) {
        if (event.getCause() == EntityDamageByEntityEvent.DamageCause.PROJECTILE) {
            profile.addKineticCharge();
            victim.getWorld().spawnParticle(Particle.CRIT, victim.getLocation().add(0, 1, 0), 5, 0.2, 0.2, 0.2, 0.1);
        }
    }

    @Override
    public void onAttack(Player attacker, LivingEntity victim, EntityDamageByEntityEvent event, PlayerAugmentProfile profile) {
        int charges = profile.getKineticCharges();
        if (charges > 0) {
            double bonus = charges * config.bonusDamagePerStack;
            event.setDamage(event.getDamage() + bonus);
            profile.resetKineticCharges();
            attacker.getWorld().spawnParticle(Particle.CRIT, victim.getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0.2);
        }
    }
}
