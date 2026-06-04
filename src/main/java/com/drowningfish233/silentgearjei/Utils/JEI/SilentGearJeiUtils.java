package com.drowningfish233.silentgearjei.Utils.JEI;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class SilentGearJeiUtils {
    public static final Pattern FORMAT_CODES = Pattern.compile("\u00a7[0-9a-z]");

    private SilentGearJeiUtils() {}


    public static List<Trait> getAllTraitsSorted() {
        List<Trait> traits = new ArrayList<>();
        List<ResourceLocation> traitIds = new ArrayList<>(SgRegistries.TRAIT.keySet());
        traitIds.sort(Comparator.comparing(id -> {
            Trait trait = SgRegistries.TRAIT.get(id);
            return trait != null ? trait.getDisplayName(1).getString() : "";
        }));

        for (ResourceLocation traitId : traitIds) {
            Trait trait = SgRegistries.TRAIT.get(traitId);
            if (trait != null) {
                traits.add(trait);
            }
        }
        return traits;
    }

    public static Component getTraitDisplayName(Trait trait) {
        return trait.getDisplayName(1);
    }

    public static Component getTraitDescription(Trait trait) {
        return trait.getDescription(1);
    }

    public static ResourceLocation getTraitId(Trait trait) {
        return SgRegistries.TRAIT.getKey(trait);
    }


    public static List<Material> getAllMaterialsSorted() {
        List<Material> materials = new ArrayList<>(SgRegistries.MATERIAL.getValues(false));
        materials.sort(Comparator.comparing(m -> {
            MaterialInstance inst = MaterialInstance.of(m);
            return inst.getDisplayName(PartTypes.MAIN.get()).getString();
        }));
        return materials;
    }

    public static Component getMaterialDisplayName(Material material, PartType partType) {
        return MaterialInstance.of(material).getDisplayName(partType);
    }

    public static ResourceLocation getMaterialId(Material material) {
        return SgRegistries.MATERIAL.getKey(material);
    }

    public static String getMaterialPackName(Material material) {
        return SgRegistries.MATERIAL.getPackName(material);
    }

    public static Material getMaterialParent(Material material) {
        return material.getParent();
    }

    public static List<String> getMaterialCategories(Material material) {
        return MaterialInstance.of(material).getCategories().stream()
                .map(cat -> cat.getDisplayName().getString())
                .collect(Collectors.toList());
    }

    public static List<PartType> getAllowedPartTypes(Material material) {
        List<PartType> allowed = new ArrayList<>();
        MaterialInstance inst = MaterialInstance.of(material);

        for (PartType partType : SgRegistries.PART_TYPE) {
            if (material.isAllowedInPart(inst, partType)) {
                allowed.add(partType);
            }
        }
        return allowed;
    }

    public static String getMaterialPropertyValue(Material material, PartType partType, GearProperty<?, ?> property) {
        MaterialInstance inst = MaterialInstance.of(material);
        var mods = inst.getPropertyModifiers(partType, PropertyKey.of(property, GearTypes.ALL.get()));

        Component formattedText = SilentGearVersionAdapter.formatTextUnchecked(mods, property);

        return FORMAT_CODES.matcher(formattedText.getString()).replaceAll("");
    }

    public static List<GearProperty<?, ?>> getAllProperties() {
        return new ArrayList<>(SgRegistries.GEAR_PROPERTY.stream().toList());
    }

    /**
     * 获取材料显示的物品栈
     * 从 MaterialInstance.getIngredient() 获取
     * 从 Material.getPartSubstitute() 获取各个部件的替代物品
     */
    public static ItemStack getMaterialDisplayStack(Material material) {
        try {
            MaterialInstance inst = MaterialInstance.of(material);

            Ingredient mainIngredient = inst.getIngredient();
            if (mainIngredient != null && !mainIngredient.isEmpty()) {
                ItemStack[] stacks = mainIngredient.getItems();
                if (stacks.length > 0 && !stacks[0].isEmpty()) {
                    return stacks[0];
                }
            }

            List<PartType> partTypes = getAllowedPartTypes(material);
            for (PartType partType : partTypes) {
                Optional<Ingredient> substitute = material.getPartSubstitute(partType);
                if (substitute.isPresent()) {
                    Ingredient ingredient = substitute.get();
                    if (!ingredient.isEmpty()) {
                        ItemStack[] stacks = ingredient.getItems();
                        if (stacks.length > 0 && !stacks[0].isEmpty()) {
                            return stacks[0];
                        }
                    }
                }
            }

            if (material.isSimple()) {
                Optional<Ingredient> mainSubstitute = material.getPartSubstitute(PartTypes.MAIN.get());
                if (mainSubstitute.isPresent()) {
                    Ingredient ingredient = mainSubstitute.get();
                    if (!ingredient.isEmpty()) {
                        ItemStack[] stacks = ingredient.getItems();
                        if (stacks.length > 0 && !stacks[0].isEmpty()) {
                            return stacks[0];
                        }
                    }
                }
            }

        } catch (Exception e) {

        }

        return ItemStack.EMPTY;
    }
}