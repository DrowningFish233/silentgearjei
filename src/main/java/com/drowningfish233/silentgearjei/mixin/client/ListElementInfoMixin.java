package com.drowningfish233.silentgearjei.mixin.client;

import com.drowningfish233.silentgearjei.compat.jei.TraitSearchableListElementInfo;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.gui.ingredients.IListElement;
import mezz.jei.gui.ingredients.IListElementInfo;
import mezz.jei.gui.ingredients.ListElementInfo;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ListElementInfo.class, remap = false)
public class ListElementInfoMixin {

    @Inject(
            method = "createFromElement",
            at = @At("RETURN"),
            cancellable = true
    )
    private static <V> void wrapWithTraitSearch(IListElement<V> element, IIngredientManager ingredientManager, IModIdHelper modIdHelper, CallbackInfoReturnable<IListElementInfo<V>> cir) {
        IListElementInfo<V> original = cir.getReturnValue();
        if (original != null && original.getTypedIngredient().getIngredient() instanceof ItemStack) {
            @SuppressWarnings("unchecked")
            IListElementInfo<ItemStack> casted = (IListElementInfo<ItemStack>) original;
            IListElementInfo<V> wrapped = (IListElementInfo<V>) new TraitSearchableListElementInfo(casted);
            cir.setReturnValue(wrapped);
        }
    }
}