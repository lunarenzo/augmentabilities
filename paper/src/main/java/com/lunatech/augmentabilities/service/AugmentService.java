package com.lunatech.augmentabilities.service;

import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.UUID;

public interface AugmentService {
    PlayerAugmentProfile getProfile(UUID uuid);
    void loadProfileAsync(UUID uuid);
    void saveProfileAsync(UUID uuid);
    void handleQuit(UUID uuid);
    void handleKill(Player killer, Player victim);
    void openAugmentMenu(Player player);
    void triggerRollMenu(Player player);
    void handleMenuClick(Player player, String menuType, int slot, List<Augment> choices);
    void cleanAllProfiles();
}
