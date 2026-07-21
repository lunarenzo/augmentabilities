package com.lunatech.augmentabilities.augment.impl;

import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentTier;
import com.lunatech.augmentabilities.config.AugmentsConfig;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public class ChronosAnchorAugment implements Augment {
    private final AugmentsConfig.ChronosAnchorConfig config;

    public ChronosAnchorAugment(AugmentsConfig.ChronosAnchorConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "CHRONOS_ANCHOR";
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
        if (player.isSneaking() && (item == null || item.getType() == Material.AIR) && !profile.isOnCooldown(getId())) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Location target = profile.getOldestRecordedPosition();
                if (target != null) {
                    player.teleport(target);
                    
                    double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
                    double missingHealth = maxHealth - player.getHealth();
                    if (missingHealth > 0) {
                        player.setHealth(Math.min(maxHealth, player.getHealth() + (missingHealth * config.healMissingHealthPercent)));
                    }

                    player.getWorld().spawnParticle(Particle.REVERSE_PORTAL, player.getLocation().add(0, 1, 0), 25, 0.4, 0.5, 0.4, 0.1);
                    profile.setCooldown(getId(), config.cooldownMs);
                    if (config.activationMessage != null && !config.activationMessage.isEmpty()) {
                        player.sendMessage(ColorParser.of(config.activationMessage).build());
                    }
                }
            }
        }
    }
}
