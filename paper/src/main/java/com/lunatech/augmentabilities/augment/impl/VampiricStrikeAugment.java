package com.lunatech.augmentabilities.augment.impl;

import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentTier;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import java.util.List;

public class VampiricStrikeAugment implements Augment {
    @Override
    public String getId() {
        return "VAMPIRIC_STRIKE";
    }

    @Override
    public String getName() {
        return "Vampiric Strike";
    }

    @Override
    public List<String> getDescription() {
        return List.of("<gray>Passive:</gray>", "<green>Critical hits</green> heal you for", "<green>15%</green> of the damage dealt.");
    }

    @Override
    public AugmentTier getTier() {
        return AugmentTier.COMMON;
    }

    @Override
    public void onAttack(Player attacker, LivingEntity victim, EntityDamageByEntityEvent event, PlayerAugmentProfile profile) {
        if (event.isCritical()) {
            double damage = event.getFinalDamage();
            double heal = damage * 0.15;
            double maxHealth = attacker.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            attacker.setHealth(Math.min(maxHealth, attacker.getHealth() + heal));
            attacker.getWorld().spawnParticle(Particle.HEART, attacker.getLocation().add(0, 1.5, 0), 3, 0.2, 0.2, 0.2, 0.05);
        }
    }
}
