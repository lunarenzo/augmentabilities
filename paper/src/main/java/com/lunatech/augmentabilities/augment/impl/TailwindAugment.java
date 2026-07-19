package com.lunatech.augmentabilities.augment.impl;

import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentTier;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.List;

public class TailwindAugment implements Augment {
    @Override
    public String getId() {
        return "TAILWIND";
    }

    @Override
    public String getName() {
        return "Tailwind";
    }

    @Override
    public List<String> getDescription() {
        return List.of("<gray>Sneak Active:</gray>", "Sneaking grants <aqua>Speed I</aqua> for <green>4s</green>.", "<dark_gray>Cooldown: 15s</dark_gray>");
    }

    @Override
    public AugmentTier getTier() {
        return AugmentTier.COMMON;
    }

    @Override
    public void onSneak(Player player, boolean isSneaking, PlayerAugmentProfile profile) {
        if (isSneaking && !profile.isOnCooldown(getId())) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 80, 0));
            player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 15, 0.3, 0.1, 0.3, 0.02);
            profile.setCooldown(getId(), 15000);
        }
    }
}
