package com.lunatech.augmentabilities.augment;

import com.lunatech.augmentabilities.augment.impl.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class AugmentRegistry {
    private static final Map<String, Augment> REGISTRY = new LinkedHashMap<>();
    private static final List<Augment> COMMONS = new ArrayList<>();
    private static final List<Augment> RARES = new ArrayList<>();
    private static final List<Augment> PRISMATICS = new ArrayList<>();

    static {
        // Common Tiers
        register(new VampiricStrikeAugment());
        register(new LastStandAugment());
        register(new TailwindAugment());

        // Rare Tiers
        register(new KineticRedirectionAugment());
        register(new ChemtechGasCloudAugment());
        register(new CounterStepAugment());

        // Prismatic Tiers
        register(new HextechOverdriveAugment());
        register(new PhaseRiftAugment());
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

    /**
     * Draws 3 unique random augments for a selection card, ensuring there is a mix of tiers:
     * e.g., 1 Prismatic/Legendary, 1 Rare, and 1 Common, or randomized based on typical weights.
     */
    public static List<Augment> rollThreeChoices(Set<String> alreadyEquipped) {
        List<Augment> choices = new ArrayList<>();
        List<Augment> pool = new ArrayList<>(REGISTRY.values());
        
        // Remove already equipped ones to avoid rolling duplicate augments
        pool.removeIf(a -> alreadyEquipped.contains(a.getId()));
        
        // If a player already has a prismatic, remove prismatics from pool so they can't roll another
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
        
        // Pick top 3 or whatever remains
        for (int i = 0; i < Math.min(3, pool.size()); i++) {
            choices.add(pool.get(i));
        }
        
        return choices;
    }
}
