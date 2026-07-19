package com.lunatech.augmentabilities.augment.impl;

import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentTier;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import java.util.List;

public class HextechOverdriveAugment implements Augment {
    @Override
    public String getId() {
        return "HEX_OVERDRIVE";
    }

    @Override
    public String getName() {
        return "Hextech Overdrive";
    }

    @Override
    public List<String> getDescription() {
        return List.of("<gray>Passive Trigger (Limit 1 Prismatic):</gray>", "Landing <yellow>5 consecutive melee hits</yellow>", "within 5s deals <red>+4 True Damage</red>", "and strikes aesthetic lightning.");
    }

    @Override
    public AugmentTier getTier() {
        return AugmentTier.PRISMATIC;
    }

    @Override
    public void onAttack(Player attacker, LivingEntity victim, EntityDamageByEntityEvent event, PlayerAugmentProfile profile) {
        profile.incrementHextechHits();
        if (profile.getHextechHits() >= 5) {
            profile.resetHextechHits();
            
            // True damage (adds bonus damage)
            double damage = event.getDamage();
            event.setDamage(damage + 4.0);

            // Cosmetic lightning and particles
            victim.getWorld().strikeLightningEffect(victim.getLocation());
            victim.getWorld().spawnParticle(Particle.CRIT, victim.getLocation().add(0, 1, 0), 20, 0.3, 0.5, 0.3, 0.2);
            attacker.sendMessage(ColorParser.of(io.github.milkdrinkers.wordweaver.Translation.of("augments.hextech.activated")).build());
        }
    }
}
