package com.lunatech.augmentabilities.service;

import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import org.bukkit.entity.Player;
import java.util.UUID;

public interface AugmentService {
    PlayerAugmentProfile getProfile(UUID uuid);
    void loadProfileAsync(UUID uuid);
    void saveProfileAsync(UUID uuid);
    void handleQuit(UUID uuid);
    void handleKill(Player killer, Player victim);
    void openAugmentMenu(Player player);
    void triggerRollMenu(Player player);
    void cleanAllProfiles();
}
