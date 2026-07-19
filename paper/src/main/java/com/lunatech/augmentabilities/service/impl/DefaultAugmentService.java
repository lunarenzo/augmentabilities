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
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
        for (Player p : Bukkit.getOnlinePlayers()) {
            loadProfileAsync(p.getUniqueId());
        }
    }

    @Override
    public void onDisable(AbstractAugmentAbilities plugin) {
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

        Inventory inv = Bukkit.createInventory(null, 27, ColorParser.of("Augment Abilities Management").build());
        AugmentMenuHolder holder = new AugmentMenuHolder("MAIN", inv, Collections.emptyList());
        inv = Bukkit.createInventory(holder, 27, ColorParser.of("<dark_gray>Augment Abilities</dark_gray>").build());

        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.displayName(Component.empty());
            filler.setItemMeta(fillerMeta);
        }
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, filler);
        }

        int[] equipSlots = {11, 13, 15};
        int slotIndex = 0;

        List<String> equippedIdsList = new ArrayList<>(profile.getEquippedAugmentIds());
        for (String id : equippedIdsList) {
            Augment aug = AugmentRegistry.getAugment(id);
            if (aug != null) {
                Material itemMat = Material.PAPER;
                if (aug.getTier() == AugmentTier.RARE) itemMat = Material.MAP;
                if (aug.getTier() == AugmentTier.PRISMATIC) itemMat = Material.NETHER_STAR;

                ItemStack item = new ItemStack(itemMat);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.displayName(ColorParser.of(aug.getTier().getColoredName() + " - " + aug.getName()).build());
                    List<Component> lore = new ArrayList<>();
                    for (String line : aug.getDescription()) {
                        lore.add(ColorParser.of(line).build());
                    }
                    lore.add(Component.empty());
                    lore.add(ColorParser.of("<red>Click to Unequip</red>").build());
                    meta.lore(lore);
                    item.setItemMeta(meta);
                }
                inv.setItem(equipSlots[slotIndex++], item);
            }
        }

        while (slotIndex < 3) {
            ItemStack item = new ItemStack(Material.BARRIER);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(ColorParser.of("<gray>Empty Slot</gray>").build());
                meta.lore(List.of(ColorParser.of("<dark_gray>No augment equipped in this slot</dark_gray>").build()));
                item.setItemMeta(meta);
            }
            inv.setItem(equipSlots[slotIndex++], item);
        }

        if (profile.getPendingRolls() > 0) {
            ItemStack item = new ItemStack(Material.GOLD_INGOT);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(ColorParser.of("<gold><b>Roll New Augment</b></gold>").build());
                meta.lore(List.of(
                    ColorParser.of("<gray>Available Rolls: <yellow>" + profile.getPendingRolls() + "</yellow></gray>").build(),
                    Component.empty(),
                    ColorParser.of("<green>Click to roll 3 random choices!</green>").build()
                ));
                item.setItemMeta(meta);
            }
            inv.setItem(22, item);
        } else {
            ItemStack item = new ItemStack(Material.COAL);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(ColorParser.of("<red>No Rolls Available</red>").build());
                meta.lore(List.of(
                    ColorParser.of("<gray>Kill <yellow>5 players</yellow> to get an augment roll.</gray>").build()
                ));
                item.setItemMeta(meta);
            }
            inv.setItem(22, item);
        }

        player.openInventory(inv);
    }

    @Override
    public void triggerRollMenu(Player player) {
        PlayerAugmentProfile profile = getProfile(player.getUniqueId());
        if (profile.getPendingRolls() <= 0) return;
        if (profile.getEquippedAugmentIds().size() >= 3) {
            player.sendMessage(ColorParser.of("<red>Cannot roll: your slots are full.</red>").build());
            return;
        }

        List<Augment> choices = AugmentRegistry.rollThreeChoices(profile.getEquippedAugmentIds());
        if (choices.isEmpty()) {
            player.sendMessage(ColorParser.of("<red>No more augments available to roll!</red>").build());
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 27, ColorParser.of("Choose 1 Augment").build());
        AugmentMenuHolder holder = new AugmentMenuHolder("ROLL", inv, choices);
        inv = Bukkit.createInventory(holder, 27, ColorParser.of("<dark_gray>Choose 1 Augment</dark_gray>").build());

        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.displayName(Component.empty());
            filler.setItemMeta(fillerMeta);
        }
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, filler);
        }

        int[] choiceSlots = {11, 13, 15};

        for (int i = 0; i < choices.size(); i++) {
            Augment aug = choices.get(i);
            Material itemMat = Material.PAPER;
            if (aug.getTier() == AugmentTier.RARE) itemMat = Material.MAP;
            if (aug.getTier() == AugmentTier.PRISMATIC) itemMat = Material.NETHER_STAR;

            ItemStack item = new ItemStack(itemMat);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(ColorParser.of(aug.getTier().getColoredName() + " - " + aug.getName()).build());
                List<Component> lore = new ArrayList<>();
                for (String line : aug.getDescription()) {
                    lore.add(ColorParser.of(line).build());
                }
                lore.add(Component.empty());
                lore.add(ColorParser.of("<green>Click to Equip</green>").build());
                meta.lore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(choiceSlots[i], item);
        }

        player.openInventory(inv);
    }

    @Override
    public void handleMenuClick(Player player, String menuType, int slot, List<Augment> choices) {
        PlayerAugmentProfile profile = getProfile(player.getUniqueId());

        if ("MAIN".equals(menuType)) {
            int[] equipSlots = {11, 13, 15};
            int listIndex = -1;
            for (int i = 0; i < equipSlots.length; i++) {
                if (equipSlots[i] == slot) {
                    listIndex = i;
                    break;
                }
            }

            if (listIndex != -1) {
                List<String> equippedIdsList = new ArrayList<>(profile.getEquippedAugmentIds());
                if (listIndex < equippedIdsList.size()) {
                    String id = equippedIdsList.get(listIndex);
                    Augment aug = AugmentRegistry.getAugment(id);
                    if (aug != null) {
                        profile.unequipAugment(id);
                        player.sendMessage(ColorParser.of("<green>Unequipped: " + aug.getName() + "</green>").build());
                        saveProfileAsync(player.getUniqueId());
                        openAugmentMenu(player);
                    }
                }
            } else if (slot == 22) {
                if (profile.getEquippedAugmentIds().size() >= 3) {
                    player.sendMessage(ColorParser.of("<red>Your slots are full! Unequip an augment first.</red>").build());
                    return;
                }
                triggerRollMenu(player);
            }
        } else if ("ROLL".equals(menuType)) {
            int[] choiceSlots = {11, 13, 15};
            int choiceIndex = -1;
            for (int i = 0; i < choiceSlots.length; i++) {
                if (choiceSlots[i] == slot) {
                    choiceIndex = i;
                    break;
                }
            }

            if (choiceIndex != -1 && choiceIndex < choices.size()) {
                Augment aug = choices.get(choiceIndex);
                if (profile.equipAugment(aug)) {
                    profile.consumePendingRoll();
                    player.sendMessage(ColorParser.of("<green>Equipped " + aug.getName() + "!</green>").build());
                    saveProfileAsync(player.getUniqueId());
                } else {
                    player.sendMessage(ColorParser.of("<red>Failed to equip! (Prismatic limit reached?)</red>").build());
                }
                player.closeInventory();
                openAugmentMenu(player);
            }
        }
    }

    @Override
    public void cleanAllProfiles() {
        profileCache.clear();
    }
}
