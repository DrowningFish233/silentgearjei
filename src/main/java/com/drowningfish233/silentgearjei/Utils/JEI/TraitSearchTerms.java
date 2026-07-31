package com.drowningfish233.silentgearjei.Utils.JEI;

import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public final class TraitSearchTerms {
    private TraitSearchTerms() {
    }

    public static Set<String> collect(ItemStack stack) {
        Set<String> terms = new LinkedHashSet<>();
        MaterialInstance material = MaterialInstance.from(stack);
        if (material != null && material.isValid()) {
            for (PartType partType : SgRegistries.PART_TYPE) {
                for (TraitInstance trait : material.getTraits(PartGearKey.ofAll(partType))) {
                    if (trait.isValid()) {
                        addSearchTerm(terms, trait.getTrait().getDisplayName(0).getString());
                        addSearchTerm(terms, trait.getTraitId().toString());
                        addSearchTerm(terms, trait.getTraitId().getPath());
                    }
                }
            }
        }
        return terms;
    }

    private static void addSearchTerm(Set<String> terms, String term) {
        if (term != null && !term.isBlank()) {
            terms.add(term.toLowerCase(Locale.ROOT));
        }
    }
}
