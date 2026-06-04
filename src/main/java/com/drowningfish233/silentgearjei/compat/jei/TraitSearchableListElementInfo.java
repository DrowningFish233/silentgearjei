package com.drowningfish233.silentgearjei.compat.jei;

import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.config.IIngredientFilterConfig;
import mezz.jei.gui.ingredients.IListElement;
import mezz.jei.gui.ingredients.IListElementInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.SgRegistries;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Stream;

public class TraitSearchableListElementInfo implements IListElementInfo<ItemStack> {

    private final IListElementInfo<ItemStack> delegate;
    private final Set<String> traitSearchTerms;

    public TraitSearchableListElementInfo(IListElementInfo<ItemStack> delegate) {
        this.delegate = delegate;
        this.traitSearchTerms = collectTraitSearchTerms(delegate.getTypedIngredient().getIngredient());
    }

    private Set<String> collectTraitSearchTerms(ItemStack stack) {
        Set<String> terms = new HashSet<>();
        MaterialInstance material = MaterialInstance.from(stack);
        if (material != null && material.isValid()) {
            for (PartType partType : SgRegistries.PART_TYPE) {
                for (TraitInstance trait : material.getTraits(PartGearKey.ofAll(partType))) {
                    if (trait.isValid()) {
                        terms.add(trait.getTrait().getDisplayName(0).getString().toLowerCase());
                        terms.add(trait.getTraitId().toString().toLowerCase());
                        terms.add(trait.getTraitId().getPath().toLowerCase());
                    }
                }
            }
        }
        return terms;
    }

    @Override
    public List<String> getNames() {
        return delegate.getNames();
    }

    @Override
    public String getModNameForSorting() {
        return delegate.getModNameForSorting();
    }

    @Override
    public List<String> getModNames() {
        return delegate.getModNames();
    }

    @Override
    public List<String> getModIds() {
        return delegate.getModIds();
    }

    @Override
    @Unmodifiable
    public Set<String> getTooltipStrings(IIngredientFilterConfig config, IIngredientManager ingredientManager) {
        Set<String> original = delegate.getTooltipStrings(config, ingredientManager);
        Set<String> combined = new HashSet<>(original);
        combined.addAll(traitSearchTerms);
        return Collections.unmodifiableSet(combined);
    }

    @Override
    public Collection<String> getTagStrings(IIngredientManager ingredientManager) {
        return delegate.getTagStrings(ingredientManager);
    }

    @Override
    public Stream<ResourceLocation> getTagIds(IIngredientManager ingredientManager) {
        return delegate.getTagIds(ingredientManager);
    }

    @Override
    public Iterable<Integer> getColors(IIngredientManager ingredientManager) {
        return delegate.getColors(ingredientManager);
    }

    @Override
    @Unmodifiable
    public Collection<String> getCreativeTabsStrings(IIngredientManager ingredientManager) {
        return delegate.getCreativeTabsStrings(ingredientManager);
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return delegate.getResourceLocation();
    }

    @Override
    public IListElement<ItemStack> getElement() {
        return delegate.getElement();
    }

    @Override
    public ITypedIngredient<ItemStack> getTypedIngredient() {
        return delegate.getTypedIngredient();
    }

    @Override
    public int getCreatedIndex() {
        return delegate.getCreatedIndex();
    }
}