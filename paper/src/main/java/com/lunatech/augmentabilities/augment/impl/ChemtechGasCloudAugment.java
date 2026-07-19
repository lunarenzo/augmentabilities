package com.lunatech.augmentabilities.augment.impl;

import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentTier;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.List;

public class ChemtechGasCloudAugment implements Augment {
    @Override
    public String getId() {
        return "CHEMTECH_CLOUD";
    }

    @Override
    public String getName() {
        return "Chemtech Gas Cloud";
    }

    @Override
    public List<String> getDescription() {
        return List.of("<gray>Right-Click Sword:</gray>", "Consumes <green>5 Durability</green> to emit", "a 2.5-block cloud of <green>Poison I</green> for 3s.", "<dark_gray>Cooldown: 30s</dark_gray>");
    }

    @Override
    public AugmentTier getTier() {
        return AugmentTier.RARE;
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event, PlayerAugmentProfile profile) {
        if (item != null && item.getType().name().endsWith("_SWORD") && !profile.isOnCooldown(getId())) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                // Damage item if it's damageable
                if (item.getItemMeta() instanceof Damageable meta) {
                    meta.setDamage(meta.getDamage() + 5);
                    item.setItemMeta(meta);
                }

                // Spawn AreaEffectCloud natively
                AreaEffectCloud cloud = player.getWorld().spawn(player.getLocation(), AreaEffectCloud.class);
                cloud.setRadius(2.5f);
                cloud.setDuration(60); // 3 seconds = 60 ticks
                cloud.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 40, 0), true);
                cloud.setSource(player);

                profile.setCooldown(getId(), 30000);
                player.sendMessage(ColorParser.of("<green>Chemtech Gas Cloud deployed!</green>").build());
            }
        }
    }
}
