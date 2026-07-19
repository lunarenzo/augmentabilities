package com.lunatech.augmentabilities.augment.impl;

import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentTier;
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
    @Override
    public String getId() {
        return "COUNTER_STEP";
    }

    @Override
    public String getName() {
        return "Counter-Step";
    }

    @Override
    public List<String> getDescription() {
        return List.of("<gray>Passive Cooldown:</gray>", "Successfully <blue>blocking</blue> an attack gives", "the attacker <light_purple>Levitation II</light_purple> for 0.75s.", "<dark_gray>Cooldown: 15s</dark_gray>");
    }

    @Override
    public AugmentTier getTier() {
        return AugmentTier.RARE;
    }

    @Override
    public void onDamageTaken(Player victim, Entity attacker, EntityDamageByEntityEvent event, PlayerAugmentProfile profile) {
        if (victim.isBlocking() && attacker instanceof LivingEntity livingAttacker && !profile.isOnCooldown(getId())) {
            livingAttacker.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 15, 1)); // 0.75s
            victim.getWorld().spawnParticle(Particle.WIND, victim.getLocation().add(0, 1, 0), 5, 0.2, 0.2, 0.2, 0.1);
            profile.setCooldown(getId(), 15000);
        }
    }
}
