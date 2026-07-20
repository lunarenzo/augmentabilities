package com.lunatech.augmentabilities.augment.impl;

import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentTier;
import com.lunatech.augmentabilities.config.AugmentsConfig;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import org.bukkit.AreaEffectCloud;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.List;

public class ChemtechGasCloudAugment implements Augment {
    private final AugmentsConfig.ChemtechGasCloudConfig config;

    public ChemtechGasCloudAugment(AugmentsConfig.ChemtechGasCloudConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "CHEMTECH_GAS_CLOUD";
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
            return AugmentTier.RARE;
        }
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event, PlayerAugmentProfile profile) {
        if (item != null && item.getType().name().endsWith("_SWORD") && !profile.isOnCooldown(getId())) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (item.getItemMeta() instanceof Damageable meta) {
                    meta.setDamage(meta.getDamage() + config.durabilityCost);
                    item.setItemMeta(meta);
                }

                AreaEffectCloud cloud = player.getWorld().spawn(player.getLocation(), AreaEffectCloud.class);
                cloud.setRadius(config.cloudRadius);
                cloud.setDuration(config.cloudDurationTicks);
                cloud.addCustomEffect(new PotionEffect(PotionEffectType.POISON, config.poisonDurationTicks, config.poisonAmplifier), true);
                cloud.setSource(player);

                profile.setCooldown(getId(), config.cooldownMs);
                if (config.activationMessage != null && !config.activationMessage.isEmpty()) {
                    player.sendMessage(ColorParser.of(config.activationMessage).build());
                }
            }
        }
    }
}
