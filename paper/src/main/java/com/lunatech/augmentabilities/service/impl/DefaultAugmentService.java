package com.lunatech.augmentabilities.service.impl;

import com.lunatech.augmentabilities.AbstractAugmentAbilities;
import com.lunatech.augmentabilities.AugmentAbilities;
import com.lunatech.augmentabilities.Reloadable;
import com.lunatech.augmentabilities.augment.Augment;
import com.lunatech.augmentabilities.augment.AugmentRegistry;
import com.lunatech.augmentabilities.augment.AugmentTier;
import com.lunatech.augmentabilities.profile.PlayerAugmentProfile;
import com.lunatech.augmentabilities.service.AugmentService;
import com.lunatech.augmentabilities.utility.DB;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.sql.Connection;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultAugmentService implements AugmentService, Reloadable {
    private final AugmentAbilities plugin;
    private final Map<UUID, PlayerAugmentProfile> profileCache = new ConcurrentHashMap<>();

    public DefaultAugmentService(AugmentAbilities plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(AbstractAugmentAbilities plugin) {
        initDatabaseTable();
    }

    @Override
    public void onEnable(AbstractAugmentAbilities plugin) {
        // Load profiles for online players if loaded/reloaded at runtime
        for (Player p : Bukkit.getOnlinePlayers()) {
            loadProfileAsync(p.getUniqueId());
        }
    }

    @Override
    public void onDisable(AbstractAugmentAbilities plugin) {
        // Flush all cached profiles to database synchronously on shutdown
        for (UUID uuid : profileCache.keySet()) {
            saveProfileSync(uuid);
        }
        profileCache.clear();
    }

    private void initDatabaseTable() {
        CompletableFuture.runAsync(() -> {
            try (Connection con = DB.getConnection()) {
                DSLContext context = DB.getContext(con);
                String prefix = DB.getHandler().getDatabaseConfig().getTablePrefix();
                context.execute("CREATE TABLE IF NOT EXISTS " + prefix + "profiles (" +
                    "uuid BINARY(16) PRIMARY KEY, " +
                    "augments VARCHAR(255) NOT NULL, " +
                    "kills INT NOT NULL DEFAULT 0, " +
                    "rolls INT NOT NULL DEFAULT 0" +
                    ")");
            } catch (Exception e) {
                plugin.getComponentLogger().error("Failed to initialize augment database table", e);
            }
        });
    }

    @Override
    public PlayerAugmentProfile getProfile(UUID uuid) {
        return profileCache.computeIfAbsent(uuid, PlayerAugmentProfile::new);
    }

    @Override
    public void loadProfileAsync(UUID uuid) {
        CompletableFuture.runAsync(() -> {
            try (Connection con = DB.getConnection()) {
                DSLContext context = DB.getContext(con);
                byte[] uuidBytes = com.lunatech.augmentabilities.database.QueryUtils.UUIDUtil.toBytes(uuid);
                String prefix = DB.getHandler().getDatabaseConfig().getTablePrefix();

                Record record = context.fetchOne(
                    "SELECT augments, kills, rolls FROM " + prefix + "profiles WHERE uuid = ?",
                    uuidBytes
                );

                PlayerAugmentProfile profile = new PlayerAugmentProfile(uuid);
                if (record != null) {
                    String augmentsStr = record.get("augments", String.class);
                    if (augmentsStr != null && !augmentsStr.isEmpty()) {
                        for (String id : augmentsStr.split(",")) {
                            Augment aug = AugmentRegistry.getAugment(id.trim());
                            if (aug != null) {
                                profile.equipAugment(aug);
                            }
                        }
                    }
                    profile.setKillCount(Objects.requireNonNullElse(record.get("kills", Integer.class), 0));
                    profile.setPendingRolls(Objects.requireNonNullElse(record.get("rolls", Integer.class), 0));
                }

                // Cache on main thread
                Bukkit.getScheduler().runTask(plugin, () -> profileCache.put(uuid, profile));
            } catch (Exception e) {
                plugin.getComponentLogger().error("Failed to load profile for " + uuid, e);
            }
        });
    }

    @Override
    public void saveProfileAsync(UUID uuid) {
        PlayerAugmentProfile profile = profileCache.get(uuid);
        if (profile == null) return;

        String augmentsStr = String.join(",", profile.getEquippedAugmentIds());
        int kills = profile.getKillCount();
        int rolls = profile.getPendingRolls();

        CompletableFuture.runAsync(() -> {
            saveToDatabase(uuid, augmentsStr, kills, rolls);
        });
    }

    private void saveProfileSync(UUID uuid) {
        PlayerAugmentProfile profile = profileCache.get(uuid);
        if (profile == null) return;

        String augmentsStr = String.join(",", profile.getEquippedAugmentIds());
        int kills = profile.getKillCount();
        int rolls = profile.getPendingRolls();

        saveToDatabase(uuid, augmentsStr, kills, rolls);
    }

    private void saveToDatabase(UUID uuid, String augmentsStr, int kills, int rolls) {
        String prefix = DB.getHandler().getDatabaseConfig().getTablePrefix();
        byte[] uuidBytes = com.lunatech.augmentabilities.database.QueryUtils.UUIDUtil.toBytes(uuid);

        try (Connection con = DB.getConnection()) {
            DSLContext context = DB.getContext(con);
            // Universal cross-dialect upsert (delete + insert)
            context.execute("DELETE FROM " + prefix + "profiles WHERE uuid = ?", uuidBytes);
            context.execute(
                "INSERT INTO " + prefix + "profiles (uuid, augments, kills, rolls) VALUES (?, ?, ?, ?)",
                uuidBytes, augmentsStr, kills, rolls
            );
        } catch (Exception e) {
            plugin.getComponentLogger().error("Failed to save profile for " + uuid, e);
        }
    }

    @Override
    public void handleQuit(UUID uuid) {
        saveProfileSync(uuid);
        profileCache.remove(uuid);
    }

    @Override
    public void handleKill(Player killer, Player victim) {
        PlayerAugmentProfile victimProfile = getProfile(victim.getUniqueId());
        boolean victimHadMaxAugments = victimProfile.getEquippedAugmentIds().size() >= 3;

        victimProfile.clearAugments();
        victim.sendMessage(MiniMessage.miniMessage().deserialize("<red>Your augments were cleared upon death!</red>"));
        saveProfileAsync(victim.getUniqueId());

        if (killer != null) {
            PlayerAugmentProfile killerProfile = getProfile(killer.getUniqueId());
            if (victimHadMaxAugments) {
                killerProfile.addPendingRoll();
                killer.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<gold>You earned an immediate Augment Roll for killing " + victim.getName() + " (who had 3 augments equipped)!</gold>"
                ));
            } else {
                killerProfile.addKill();
                if (killerProfile.getKillCount() >= 5) {
                    killerProfile.resetKillCount();
                    killerProfile.addPendingRoll();
                    killer.sendMessage(MiniMessage.miniMessage().deserialize(
                        "<gold><b>5 KILLS REACHED!</b> You earned 1 Augment Roll. Sneak + Left-Click with empty hand or type /augment to open.</gold>"
                    ));
                } else {
                    int remaining = 5 - killerProfile.getKillCount();
                    killer.sendMessage(MiniMessage.miniMessage().deserialize(
                        "<gray>Kill registered. <yellow>" + remaining + "</yellow> more kills for an Augment Roll.</gray>"
                    ));
                }
            }
            saveProfileAsync(killer.getUniqueId());
        }
    }

    @Override
    public void openAugmentMenu(Player player) {
        PlayerAugmentProfile profile = getProfile(player.getUniqueId());

        Gui gui = Gui.gui()
            .title(Component.text("Augment Abilities Management"))
            .rows(3)
            .disableAllInteractions()
            .create();

        gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());

        int[] equipSlots = {11, 13, 15};
        int slotIndex = 0;

        for (String id : profile.getEquippedAugmentIds()) {
            Augment aug = AugmentRegistry.getAugment(id);
            if (aug != null) {
                List<Component> lore = new ArrayList<>();
                for (String line : aug.getDescription()) {
                    lore.add(MiniMessage.miniMessage().deserialize(line));
                }
                lore.add(Component.empty());
                lore.add(MiniMessage.miniMessage().deserialize("<red>Click to Unequip</red>"));

                Material itemMat = Material.PAPER;
                if (aug.getTier() == AugmentTier.RARE) itemMat = Material.MAP;
                if (aug.getTier() == AugmentTier.PRISMATIC) itemMat = Material.NETHER_STAR;

                GuiItem guiItem = ItemBuilder.from(itemMat)
                    .name(MiniMessage.miniMessage().deserialize(aug.getTier().getColoredName() + " - " + aug.getName()))
                    .lore(lore)
                    .asGuiItem(event -> {
                        profile.unequipAugment(aug.getId());
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Unequipped: " + aug.getName() + "</green>"));
                        saveProfileAsync(player.getUniqueId());
                        openAugmentMenu(player);
                    });

                gui.setItem(equipSlots[slotIndex++], guiItem);
            }
        }

        while (slotIndex < 3) {
            GuiItem emptyItem = ItemBuilder.from(Material.BARRIER)
                .name(MiniMessage.miniMessage().deserialize("<gray>Empty Slot</gray>"))
                .lore(MiniMessage.miniMessage().deserialize("<dark_gray>No augment equipped in this slot</dark_gray>"))
                .asGuiItem();
            gui.setItem(equipSlots[slotIndex++], emptyItem);
        }

        if (profile.getPendingRolls() > 0) {
            GuiItem rollItem = ItemBuilder.from(Material.GOLD_INGOT)
                .name(MiniMessage.miniMessage().deserialize("<gold><b>Roll New Augment</b></gold>"))
                .lore(
                    MiniMessage.miniMessage().deserialize("<gray>Available Rolls: <yellow>" + profile.getPendingRolls() + "</yellow></gray>"),
                    Component.empty(),
                    MiniMessage.miniMessage().deserialize("<green>Click to roll 3 random choices!</green>")
                )
                .asGuiItem(event -> {
                    if (profile.getEquippedAugmentIds().size() >= 3) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Your slots are full! Unequip an augment first.</red>"));
                        return;
                    }
                    gui.close(player);
                    triggerRollMenu(player);
                });
            gui.setItem(22, rollItem);
        } else {
            GuiItem noRollItem = ItemBuilder.from(Material.COAL)
                .name(MiniMessage.miniMessage().deserialize("<red>No Rolls Available</red>"))
                .lore(MiniMessage.miniMessage().deserialize("<gray>Kill <yellow>5 players</yellow> to get an augment roll.</gray>"))
                .asGuiItem();
            gui.setItem(22, noRollItem);
        }

        gui.open(player);
    }

    @Override
    public void triggerRollMenu(Player player) {
        PlayerAugmentProfile profile = getProfile(player.getUniqueId());
        if (profile.getPendingRolls() <= 0) return;
        if (profile.getEquippedAugmentIds().size() >= 3) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Cannot roll: your slots are full.</red>"));
            return;
        }

        List<Augment> choices = AugmentRegistry.rollThreeChoices(profile.getEquippedAugmentIds());
        if (choices.isEmpty()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>No more augments available to roll!</red>"));
            return;
        }

        Gui gui = Gui.gui()
            .title(Component.text("Choose 1 Augment"))
            .rows(3)
            .disableAllInteractions()
            .create();

        gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());

        int[] choiceSlots = {11, 13, 15};

        for (int i = 0; i < choices.size(); i++) {
            Augment aug = choices.get(i);
            List<Component> lore = new ArrayList<>();
            for (String line : aug.getDescription()) {
                lore.add(MiniMessage.miniMessage().deserialize(line));
            }
            lore.add(Component.empty());
            lore.add(MiniMessage.miniMessage().deserialize("<green>Click to Equip</green>"));

            Material itemMat = Material.PAPER;
            if (aug.getTier() == AugmentTier.RARE) itemMat = Material.MAP;
            if (aug.getTier() == AugmentTier.PRISMATIC) itemMat = Material.NETHER_STAR;

            GuiItem guiItem = ItemBuilder.from(itemMat)
                .name(MiniMessage.miniMessage().deserialize(aug.getTier().getColoredName() + " - " + aug.getName()))
                .lore(lore)
                .asGuiItem(event -> {
                    if (profile.equipAugment(aug)) {
                        profile.consumePendingRoll();
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Equipped " + aug.getName() + "!</green>"));
                        saveProfileAsync(player.getUniqueId());
                    } else {
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Failed to equip! (Prismatic limit reached?)</red>"));
                    }
                    gui.close(player);
                    openAugmentMenu(player);
                });
            gui.setItem(choiceSlots[i], guiItem);
        }

        gui.open(player);
    }

    @Override
    public void cleanAllProfiles() {
        profileCache.clear();
    }
}
