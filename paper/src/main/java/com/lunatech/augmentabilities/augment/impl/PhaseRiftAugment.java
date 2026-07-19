package com.lunatech.augmentabilities.augment.impl;

import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentTier;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import java.util.List;

public class PhaseRiftAugment implements Augment {
    @Override
    public String getId() {
        return "PHASE_RIFT";
    }

    @Override
    public String getName() {
        return "Phase Rift";
    }

    @Override
    public List<String> getDescription() {
        return List.of("<gray>Sneak-Sprint (Limit 1 Prismatic):</gray>", "Sneaking while sprinting <light_purple>teleports</light_purple>", "you 5 blocks forward through spaces.", "<dark_gray>Cooldown: 20s</dark_gray>");
    }

    @Override
    public AugmentTier getTier() {
        return AugmentTier.PRISMATIC;
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
                    return; // No direction found
                }
            }

            Location target = loc.clone().add(direction.clone().multiply(5));
            Block targetBlock = target.getBlock();
            Block headBlock = targetBlock.getRelative(0, 1, 0);

            // Simple check to make sure they don't teleport inside a solid wall
            if (targetBlock.getType().isSolid() || headBlock.getType().isSolid()) {
                // Raycast back to find last air space
                for (int i = 4; i > 0; i--) {
                    Location check = loc.clone().add(direction.clone().multiply(i));
                    if (!check.getBlock().getType().isSolid() && !check.getBlock().getRelative(0, 1, 0).getType().isSolid()) {
                        target = check;
                        break;
                    }
                }
            }

            // Keep pitch/yaw from original position
            target.setPitch(loc.getPitch());
            target.setYaw(loc.getYaw());

            player.teleport(target);
            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 30, 0.5, 1, 0.5, 0.1);
            profile.setCooldown(getId(), 20000);
        }
    }
}
