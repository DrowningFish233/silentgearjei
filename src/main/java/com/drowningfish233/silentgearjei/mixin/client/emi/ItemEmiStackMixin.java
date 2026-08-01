package com.drowningfish233.silentgearjei.mixin.client.emi;

import dev.emi.emi.api.stack.ItemEmiStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.SgRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(value = ItemEmiStack.class, remap = false)
@OnlyIn(Dist.CLIENT)
public class ItemEmiStackMixin {

    private static final boolean EMI_PRESENT;
    private static final String TRAIT_PREFIX = "%";
    private static final String PART_TYPE_PREFIX = "&";

    static {
        boolean present = false;
        try {
            Class.forName("dev.emi.emi.EmiPort");
            present = true;
        } catch (ClassNotFoundException e) {
            present = false;
        }
        EMI_PRESENT = present;
    }

    @Inject(method = "getTooltipText", at = @At("RETURN"), cancellable = true)
    private void addTraitSearchTerms(CallbackInfoReturnable<List<Component>> cir) {
        if (!EMI_PRESENT) return;

        ItemEmiStack self = (ItemEmiStack) (Object) this;
        ItemStack stack = self.getItemStack();

        MaterialInstance material = MaterialInstance.from(stack);
        if (material != null && material.isValid()) {
            Set<String> searchTerms = new HashSet<>();

            collectTraitSearchTerms(material, searchTerms);

            collectPartTypeSearchTerms(material, searchTerms);

            if (!searchTerms.isEmpty()) {
                List<Component> original = cir.getReturnValue();
                List<Component> modified = new ArrayList<>(original);
                for (String term : searchTerms) {
                    modified.add(Component.literal(term));
                }
                cir.setReturnValue(modified);
            }
        }
    }

    private void collectTraitSearchTerms(MaterialInstance material, Set<String> terms) {
        for (PartType partType : SgRegistries.PART_TYPE) {
            for (TraitInstance trait : material.getTraits(PartGearKey.ofAll(partType))) {
                if (trait.isValid()) {
                    String displayName = trait.getTrait().getDisplayName(0).getString();
                    terms.add(TRAIT_PREFIX + displayName);
                    terms.add(TRAIT_PREFIX + displayName.toLowerCase());

                    String fullId = trait.getTraitId().toString();
                    terms.add(TRAIT_PREFIX + fullId);
                    terms.add(TRAIT_PREFIX + fullId.toLowerCase());

                    String path = trait.getTraitId().getPath();
                    terms.add(TRAIT_PREFIX + path);
                    terms.add(TRAIT_PREFIX + path.toLowerCase());
                }
            }
        }
    }

    private void collectPartTypeSearchTerms(MaterialInstance material, Set<String> terms) {
        Set<PartType> partTypes = material.getPartTypes();
        for (PartType partType : partTypes) {
            ResourceLocation partTypeId = SgRegistries.PART_TYPE.getKey(partType);
            if (partTypeId != null && !partTypeId.getPath().equals("none")) {
                String displayName = partType.getDisplayName().getString();
                terms.add(PART_TYPE_PREFIX + displayName);
                terms.add(PART_TYPE_PREFIX + displayName.toLowerCase());

                String path = partTypeId.getPath();
                terms.add(PART_TYPE_PREFIX + path);
                terms.add(PART_TYPE_PREFIX + path.toLowerCase());

                String fullId = partTypeId.toString();
                terms.add(PART_TYPE_PREFIX + fullId);
                terms.add(PART_TYPE_PREFIX + fullId.toLowerCase());
            }
        }
    }
}