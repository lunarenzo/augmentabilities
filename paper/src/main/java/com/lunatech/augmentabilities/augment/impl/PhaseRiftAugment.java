package com.lunatech.augmentabilities.augment.impl;

import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentTier;
import com.lunatech.augmentabilities.config.AugmentsConfig;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import java.util.List;

public class PhaseRiftAugment implements Augment {
    private final AugmentsConfig.PhaseRiftConfig config;

    public PhaseRiftAugment(AugmentsConfig.PhaseRiftConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "PHASE_RIFT";
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
    public void onSneak(Player player, boolean isSneaking, PlayerAugmentProfile profile) {
        if (isSneaking && player.isSprinting() && !profile.isOnCooldown(getId())) {
            Location loc = player.getLocation();
            Vector direction = loc.getDirection().setY(0);
            if (direction.lengthSquared() > 0) {
                direction.normalize();
            } else {
                direction = player.getVelocity().setY(0);
                if (direction.lengthSquared() > 0) {
                    direction.normalize();
                } else {
                    return;
                }
            }

            Location target = loc.clone().add(direction.clone().multiply(config.teleportDistance));
            Block targetBlock = target.getBlock();
            Block headBlock = targetBlock.getRelative(0, 1, 0);

            if (targetBlock.getType().isSolid() || headBlock.getType().isSolid()) {
                int dist = (int) Math.floor(config.teleportDistance) - 1;
                for (int i = dist; i > 0; i--) {
                    Location check = loc.clone().add(direction.clone().multiply(i));
                    if (!check.getBlock().getType().isSolid() && !check.getBlock().getRelative(0, 1, 0).getType().isSolid()) {
                        target = check;
                        break;
                    }
                }
            }

            target.setPitch(loc.getPitch());
            target.setYaw(loc.getYaw());

            player.teleport(target);
            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 30, 0.5, 1, 0.5, 0.1);
            profile.setCooldown(getId(), config.cooldownMs);
        }
    }
}
