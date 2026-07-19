package com.lunatech.augmentabilities.augment;

import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public interface Augment {
    String getId();
    String getName();
    List<String> getDescription();
    AugmentTier getTier();

    default void onAttack(Player attacker, LivingEntity victim, EntityDamageByEntityEvent event, PlayerAugmentProfile profile) {}
    default void onDamageTaken(Player victim, Entity attacker, EntityDamageByEntityEvent event, PlayerAugmentProfile profile) {}
    default void onSneak(Player player, boolean isSneaking, PlayerAugmentProfile profile) {}
    default void onRightClick(Player player, ItemStack item, PlayerInteractEvent event, PlayerAugmentProfile profile) {}
}
