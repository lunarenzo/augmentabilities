package com.lunatech.augmentabilities.augment.impl;

import com.lunatech.augmentabilities.AugmentAbilities;
import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentTier;
import com.lunatech.augmentabilities.config.AugmentsConfig;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import java.util.List;

public class VoidSingularityAugment implements Augment {
    private final AugmentsConfig.VoidSingularityConfig config;

    public VoidSingularityAugment(AugmentsConfig.VoidSingularityConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "VOID_SINGULARITY";
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
    public boolean isEnabled() {
        return config.enabled;
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event, PlayerAugmentProfile profile) {
        if (player.isSneaking() && item != null && item.getType().name().endsWith("_SWORD") && !profile.isOnCooldown(getId())) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block targetBlock = player.getTargetBlockExact((int) Math.ceil(config.castRange));
                Location center = targetBlock != null ? targetBlock.getLocation().add(0.5, 1, 0.5) : player.getLocation().add(player.getLocation().getDirection().multiply(config.castRange));

                profile.setCooldown(getId(), config.cooldownMs);
                if (config.activationMessage != null && !config.activationMessage.isEmpty()) {
                    player.sendMessage(ColorParser.of(config.activationMessage).build());
                }

                JavaPlugin plugin = JavaPlugin.getPlugin(AugmentAbilities.class);
                double radius = config.pullRadius;

                // Vortex pull task
                for (int t = 0; t < 25; t += 5) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        center.getWorld().spawnParticle(Particle.PORTAL, center, 20, radius / 2, 0.5, radius / 2, 0.1);
                        for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
                            if (entity instanceof LivingEntity target && target != player) {
                                Vector pull = center.toVector().subtract(target.getLocation().toVector()).setY(0.2);
                                if (pull.lengthSquared() > 0) {
                                    pull.normalize();
                                }
                                target.setVelocity(pull.multiply(0.5));
                            }
                        }
                    }, t);
                }

                // Final detonation
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    center.getWorld().spawnParticle(Particle.LARGE_SMOKE, center, 40, 1.0, 1.0, 1.0, 0.2);
                    center.getWorld().spawnParticle(Particle.DRAGON_BREATH, center, 30, 0.5, 0.5, 0.5, 0.1);
                    for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
                        if (entity instanceof LivingEntity target && target != player) {
                            target.damage(config.detonationTrueDamage, player);
                        }
                    }
                }, 30L);
            }
        }
    }
}
