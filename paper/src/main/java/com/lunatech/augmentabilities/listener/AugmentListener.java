package com.lunatech.augmentabilities.listener;

import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentRegistry;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import com.lunatech.augmentabilities.service.AugmentService;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import com.lunatech.augmentabilities.service.impl.AugmentMenuHolder;

public class AugmentListener implements Listener {
    private final AugmentService service;

    public AugmentListener(AugmentService service) {
        this.service = service;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        service.loadProfileAsync(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        service.handleQuit(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCombatAttack(EntityDamageByEntityEvent event) {
        // Attacker checks
        if (event.getDamager() instanceof Player attacker) {
            if (event.getEntity() instanceof LivingEntity victim) {
                PlayerAugmentProfile profile = service.getProfile(attacker.getUniqueId());
                for (String id : profile.getEquippedAugmentIds()) {
                    Augment aug = AugmentRegistry.getAugment(id);
                    if (aug != null) {
                        aug.onAttack(attacker, victim, event, profile);
                    }
                }
            }
        }

        // Victim checks
        if (event.getEntity() instanceof Player victim) {
            PlayerAugmentProfile profile = service.getProfile(victim.getUniqueId());
            for (String id : profile.getEquippedAugmentIds()) {
                Augment aug = AugmentRegistry.getAugment(id);
                if (aug != null) {
                    aug.onDamageTaken(victim, event.getDamager(), event, profile);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        PlayerAugmentProfile profile = service.getProfile(player.getUniqueId());
        
        for (String id : profile.getEquippedAugmentIds()) {
            Augment aug = AugmentRegistry.getAugment(id);
            if (aug != null) {
                aug.onSneak(player, event.isSneaking(), profile);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        // Sneak + Left-Click with empty hand opens selection menu
        if (player.isSneaking() && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                event.setCancelled(true);
                service.openAugmentMenu(player);
                return;
            }
        }

        // Active right click triggers for augments
        PlayerAugmentProfile profile = service.getProfile(player.getUniqueId());
        for (String id : profile.getEquippedAugmentIds()) {
            Augment aug = AugmentRegistry.getAugment(id);
            if (aug != null) {
                aug.onRightClick(player, event.getItem(), event, profile);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        service.handleKill(killer, victim);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof AugmentMenuHolder holder) {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player player)) return;

            int slot = event.getRawSlot();
            service.handleMenuClick(player, holder.getType(), slot, holder.getChoices());
        }
    }
}
