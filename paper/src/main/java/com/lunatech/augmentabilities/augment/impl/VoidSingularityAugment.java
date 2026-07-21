package com.lunatech.augmentabilities.augment.impl;

import com.lunatech.augmentabilities.AugmentAbilities;
import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentTier;
import com.lunatech.augmentabilities.config.AugmentsConfig;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import org.bukkit.Bukkit;
import org.bukkit.Color;
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
                double baseRadius = config.pullRadius;

                // 1. Phased Swirling Vortex Pull (Ticks 0 to 25)
                for (int tick = 0; tick <= 25; tick += 2) {
                    final int t = tick;
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        double progress = t / 25.0; // 0.0 -> 1.0
                        double currentRadius = baseRadius * (1.0 - (progress * progress)); // Eased Inward Collapse Radius
                        double theta = t * 0.6; // Angular rotation speed

                        // Dual Inward Helices (180deg phase difference)
                        double x1 = currentRadius * Math.cos(theta);
                        double z1 = currentRadius * Math.sin(theta);
                        double x2 = currentRadius * Math.cos(theta + Math.PI);
                        double z2 = currentRadius * Math.sin(theta + Math.PI);

                        Location p1 = center.clone().add(x1, 0.2 + (progress * 0.5), z1);
                        Location p2 = center.clone().add(x2, 0.2 + (progress * 0.5), z2);

                        // Safe particles
                        spawnSafeParticle(p1, Particle.PORTAL, 6, 0.05, 0.05, 0.05, 0.02);
                        spawnSafeParticle(p2, Particle.PORTAL, 6, 0.05, 0.05, 0.05, 0.02);

                        // Dark Abyssal Core Ambient
                        spawnSafeParticle(center, Particle.WITCH, 8, 0.2, 0.3, 0.2, 0.02);
                        spawnSafeParticle(center, Particle.SOUL_FIRE_FLAME, 4, 0.1, 0.1, 0.1, 0.01);
                        spawnSafeParticle(center, Particle.LARGE_SMOKE, 3, 0.1, 0.2, 0.1, 0.01);

                        // Accelerating Gravitational Pull
                        double pullMultiplier = 0.35 + (progress * 0.40);
                        for (Entity entity : center.getWorld().getNearbyEntities(center, baseRadius, baseRadius, baseRadius)) {
                            if (entity instanceof LivingEntity target && target != player) {
                                Vector pull = center.toVector().subtract(target.getLocation().toVector()).setY(0.25);
                                if (pull.lengthSquared() > 0) {
                                    pull.normalize();
                                }
                                target.setVelocity(pull.multiply(pullMultiplier));
                            }
                        }
                    }, t);
                }

                // 2. Violent Prismatic Detonation & Shockwave (Tick 30)
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    // Multi-layer Detonation: Sonic Boom + Explosion + Soul Flame Burst + Dark Smoke
                    spawnSafeParticle(center, Particle.SONIC_BOOM, 1, 0, 0, 0, 0);
                    spawnSafeParticle(center, Particle.EXPLOSION, 4, 0.2, 0.2, 0.2, 0.05);
                    spawnSafeParticle(center, Particle.SOUL_FIRE_FLAME, 35, 1.0, 0.8, 1.0, 0.15);
                    spawnSafeParticle(center, Particle.LARGE_SMOKE, 25, 0.8, 0.5, 0.8, 0.1);

                    // Apply True Damage to trapped enemies
                    for (Entity entity : center.getWorld().getNearbyEntities(center, baseRadius, baseRadius, baseRadius)) {
                        if (entity instanceof LivingEntity target && target != player) {
                            target.damage(config.detonationTrueDamage, player);
                        }
                    }
                }, 30L);
            }
        }
    }

    private void spawnSafeParticle(Location location, Particle particle, int count, double offsetX, double offsetY, double offsetZ, double speed) {
        try {
            if (particle.getDataType() == Float.class) {
                location.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed, 1.0f);
            } else if (particle.getDataType() == Color.class) {
                location.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed, Color.PURPLE);
            } else {
                location.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed);
            }
        } catch (Throwable ignored) {
            // Fail silently without interrupting task execution or damage application
        }
    }
}
