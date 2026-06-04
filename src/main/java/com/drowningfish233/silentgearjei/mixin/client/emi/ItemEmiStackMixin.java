package com.drowningfish233.silentgearjei.mixin.client.emi;

import dev.emi.emi.api.stack.ItemEmiStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
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
public class ItemEmiStackMixin {

    private static final boolean EMI_PRESENT;

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
            Set<String> traitNames = new HashSet<>();
            for (PartType partType : SgRegistries.PART_TYPE) {
                for (TraitInstance trait : material.getTraits(PartGearKey.ofAll(partType))) {
                    if (trait.isValid()) {
                        traitNames.add(trait.getTrait().getDisplayName(0).getString().toLowerCase());
                        traitNames.add(trait.getTraitId().toString().toLowerCase());
                        traitNames.add(trait.getTraitId().getPath().toLowerCase());
                    }
                }
            }

            if (!traitNames.isEmpty()) {
                List<Component> original = cir.getReturnValue();
                List<Component> modified = new ArrayList<>(original);
                for (String name : traitNames) {
                    modified.add(Component.literal(name));
                }
                cir.setReturnValue(modified);
            }
        }
    }
}