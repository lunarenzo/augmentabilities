package com.lunatech.augmentabilities.augment;

import com.lunatech.augmentabilities.config.AugmentsConfig;
import com.lunatech.augmentabilities.augment.impl.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class AugmentRegistry {
    private static final Map<String, Augment> REGISTRY = new LinkedHashMap<>();
    private static final List<Augment> COMMONS = new ArrayList<>();
    private static final List<Augment> RARES = new ArrayList<>();
    private static final List<Augment> PRISMATICS = new ArrayList<>();

    static {
        init(new AugmentsConfig());
    }

    public static void init(AugmentsConfig config) {
        REGISTRY.clear();
        COMMONS.clear();
        RARES.clear();
        PRISMATICS.clear();

        if (config == null) {
            config = new AugmentsConfig();
        }

        // Common Tiers
        register(new VampiricStrikeAugment(config.vampiricStrike));
        register(new LastStandAugment(config.lastStand));
        register(new TailwindAugment(config.tailwind));

        // Rare Tiers
        register(new KineticRedirectionAugment(config.kineticRedirection));
        register(new ChemtechGasCloudAugment(config.chemtechGasCloud));
        register(new CounterStepAugment(config.counterStep));

        // Prismatic Tiers
        register(new HextechOverdriveAugment(config.hextechOverdrive));
        register(new PhaseRiftAugment(config.phaseRift));
    }

    private static void register(Augment augment) {
        REGISTRY.put(augment.getId(), augment);
        switch (augment.getTier()) {
            case COMMON -> COMMONS.add(augment);
            case RARE -> RARES.add(augment);
            case PRISMATIC -> PRISMATICS.add(augment);
        }
    }

    public static Augment getAugment(String id) {
        return REGISTRY.get(id);
    }

    public static Collection<Augment> getAllAugments() {
        return REGISTRY.values();
    }

    public static List<Augment> rollThreeChoices(Set<String> alreadyEquipped) {
        List<Augment> choices = new ArrayList<>();
        List<Augment> pool = new ArrayList<>(REGISTRY.values());

        pool.removeIf(a -> alreadyEquipped.contains(a.getId()));

        boolean hasPrismatic = false;
        for (String equippedId : alreadyEquipped) {
            Augment eq = getAugment(equippedId);
            if (eq != null && eq.getTier() == AugmentTier.PRISMATIC) {
                hasPrismatic = true;
                break;
            }
        }

        if (hasPrismatic) {
            pool.removeIf(a -> a.getTier() == AugmentTier.PRISMATIC);
        }

        if (pool.isEmpty()) {
            return Collections.emptyList();
        }

        Collections.shuffle(pool, ThreadLocalRandom.current());

        for (int i = 0; i < Math.min(3, pool.size()); i++) {
            choices.add(pool.get(i));
        }

        return choices;
    }
}
