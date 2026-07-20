package com.lunatech.augmentabilities.augment.impl;

import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentTier;
import com.lunatech.augmentabilities.config.AugmentsConfig;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import java.util.List;

public class HextechOverdriveAugment implements Augment {
    private final AugmentsConfig.HextechOverdriveConfig config;

    public HextechOverdriveAugment(AugmentsConfig.HextechOverdriveConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "HEXTECH_OVERDRIVE";
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
            return AugmentTier.PRISMATIC;
        }
    }

    @Override
    public void onAttack(Player attacker, LivingEntity victim, EntityDamageByEntityEvent event, PlayerAugmentProfile profile) {
        profile.incrementHextechHits();
        if (profile.getHextechHits() >= config.requiredHits) {
            profile.resetHextechHits();
            
            double damage = event.getDamage();
            event.setDamage(damage + config.bonusTrueDamage);

            victim.getWorld().strikeLightningEffect(victim.getLocation());
            victim.getWorld().spawnParticle(Particle.CRIT, victim.getLocation().add(0, 1, 0), 20, 0.3, 0.5, 0.3, 0.2);
            if (config.activationMessage != null && !config.activationMessage.isEmpty()) {
                attacker.sendMessage(ColorParser.of(config.activationMessage).build());
            }
        }
    }
}
