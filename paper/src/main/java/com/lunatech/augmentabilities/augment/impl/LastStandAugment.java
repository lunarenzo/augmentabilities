package com.lunatech.augmentabilities.augment.impl;

import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentTier;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.List;

public class LastStandAugment implements Augment {
    @Override
    public String getId() {
        return "LAST_STAND";
    }

    @Override
    public String getName() {
        return "Last Stand";
    }

    @Override
    public List<String> getDescription() {
        return List.of("<gray>Passive Cooldown:</gray>", "Grants <blue>Resistance II</blue> for <green>3s</green>", "when falling below <red>3 hearts</red>.", "<dark_gray>Cooldown: 60s</dark_gray>");
    }

    @Override
    public AugmentTier getTier() {
        return AugmentTier.COMMON;
    }

    @Override
    public void onDamageTaken(Player victim, Entity attacker, EntityDamageByEntityEvent event, PlayerAugmentProfile profile) {
        double finalHealth = victim.getHealth() - event.getFinalDamage();
        if (finalHealth < 6.0 && !profile.isOnCooldown(getId())) {
            victim.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 60, 1));
            victim.getWorld().spawnParticle(Particle.CRIT, victim.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.05);
            profile.setCooldown(getId(), 60000);
            victim.sendMessage(ColorParser.of("<red>Last Stand activated!</red>").build());
        }
    }
}
