package com.drowningfish233.silentgearjei.Utils.JEI;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public final class PartTypeSearchTerms {
    private PartTypeSearchTerms() {
    }

    public static Set<String> collect(ItemStack stack) {
        Set<String> terms = new LinkedHashSet<>();

        MaterialInstance material = MaterialInstance.from(stack);

        if (material == null || !material.isValid()) {
            var mat = SgRegistries.MATERIAL.fromItem(stack);
            if (mat != null) {
                material = MaterialInstance.of(mat, stack);
            }
        }

        if (material != null && material.isValid()) {
            Set<PartType> partTypes = material.getPartTypes();
            for (PartType partType : partTypes) {
                ResourceLocation partTypeId = SgRegistries.PART_TYPE.getKey(partType);
                if (partTypeId != null && !partTypeId.getPath().equals("none")) {
                    String path = partTypeId.getPath();
                    addSearchTerm(terms, path);

                    String displayName = partType.getDisplayName().getString();
                    addSearchTerm(terms, displayName);

                    String fullId = partTypeId.toString();
                    addSearchTerm(terms, fullId);
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