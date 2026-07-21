package com.lunatech.augmentabilities.profile;

import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentRegistry;
import com.lunatech.augmentabilities.augment.AugmentTier;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class PlayerAugmentProfile {
    private final UUID playerUuid;
    private final Set<String> equippedAugmentIds = new HashSet<>(3);
    private final Map<String, Long> cooldowns = new HashMap<>();

    // Combat tracking fields
    private int killCount = 0;
    private int pendingRolls = 0;

    // Hextech Overdrive state
    private int hextechHits = 0;
    private long lastHextechHit = 0;

    // Kinetic Redirection state
    private int kineticCharges = 0;
    private long lastKineticHit = 0;

    // Frostbite Strike state
    private final Map<UUID, Integer> frostbiteHits = new HashMap<>();
    private long lastFrostbiteHit = 0;

    // Chronos Anchor position ring buffer
    private final org.bukkit.Location[] positionHistory = new org.bukkit.Location[5];
    private int positionHistoryIndex = 0;

    public PlayerAugmentProfile(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public Set<String> getEquippedAugmentIds() {
        return equippedAugmentIds;
    }

    public boolean hasAugment(String id) {
        return equippedAugmentIds.contains(id);
    }

    public boolean equipAugment(Augment augment) {
        if (equippedAugmentIds.size() >= 3) {
            return false;
        }
        if (augment.getTier() == AugmentTier.PRISMATIC) {
            for (String equippedId : equippedAugmentIds) {
                Augment eq = AugmentRegistry.getAugment(equippedId);
                if (eq != null && eq.getTier() == AugmentTier.PRISMATIC) {
                    return false; // Only 1 prismatic/legendary is equipable
                }
            }
        }
        return equippedAugmentIds.add(augment.getId());
    }

    public boolean unequipAugment(String id) {
        return equippedAugmentIds.remove(id);
    }

    public void clearAugments() {
        equippedAugmentIds.clear();
        cooldowns.clear();
        hextechHits = 0;
        kineticCharges = 0;
    }

    // Cooldown management
    public boolean isOnCooldown(String id) {
        return cooldowns.getOrDefault(id, 0L) > System.currentTimeMillis();
    }

    public long getRemainingCooldown(String id) {
        return Math.max(0, cooldowns.getOrDefault(id, 0L) - System.currentTimeMillis());
    }

    public void setCooldown(String id, long durationMs) {
        cooldowns.put(id, System.currentTimeMillis() + durationMs);
    }

    // Progression
    public int getKillCount() {
        return killCount;
    }

    public void addKill() {
        this.killCount++;
    }

    public void resetKillCount() {
        this.killCount = 0;
    }

    public void setKillCount(int killCount) {
        this.killCount = killCount;
    }

    public int getPendingRolls() {
        return pendingRolls;
    }

    public void addPendingRoll() {
        this.pendingRolls++;
    }

    public void consumePendingRoll() {
        if (this.pendingRolls > 0) {
            this.pendingRolls--;
        }
    }

    public void setPendingRolls(int rolls) {
        this.pendingRolls = rolls;
    }

    // Hextech Overdrive helper states
    public int getHextechHits() {
        // Expire hits if not hit within 5 seconds
        if (System.currentTimeMillis() - lastHextechHit > 5000) {
            hextechHits = 0;
        }
        return hextechHits;
    }

    public void incrementHextechHits() {
        if (System.currentTimeMillis() - lastHextechHit > 5000) {
            hextechHits = 0;
        }
        hextechHits++;
        lastHextechHit = System.currentTimeMillis();
    }

    public void resetHextechHits() {
        hextechHits = 0;
    }

    // Kinetic Redirection helper states
    public int getKineticCharges() {
        if (System.currentTimeMillis() - lastKineticHit > 5000) {
            kineticCharges = 0;
        }
        return kineticCharges;
    }

    public void addKineticCharge() {
        if (System.currentTimeMillis() - lastKineticHit > 5000) {
            kineticCharges = 0;
        }
        if (kineticCharges < 3) {
            kineticCharges++;
        }
        lastKineticHit = System.currentTimeMillis();
    }

    public void resetKineticCharges() {
        kineticCharges = 0;
    }

    // Frostbite Strike helper methods
    public int incrementFrostbiteHits(UUID victimId) {
        if (System.currentTimeMillis() - lastFrostbiteHit > 4000) {
            frostbiteHits.clear();
        }
        lastFrostbiteHit = System.currentTimeMillis();
        int hits = frostbiteHits.getOrDefault(victimId, 0) + 1;
        frostbiteHits.put(victimId, hits);
        return hits;
    }

    public void resetFrostbiteHits(UUID victimId) {
        frostbiteHits.remove(victimId);
    }

    // Chronos Anchor helper methods
    public void recordPosition(org.bukkit.Location loc) {
        positionHistory[positionHistoryIndex] = loc.clone();
        positionHistoryIndex = (positionHistoryIndex + 1) % 5;
    }

    public org.bukkit.Location getOldestRecordedPosition() {
        org.bukkit.Location loc = positionHistory[positionHistoryIndex];
        return loc != null ? loc : positionHistory[(positionHistoryIndex + 4) % 5];
    }
}
