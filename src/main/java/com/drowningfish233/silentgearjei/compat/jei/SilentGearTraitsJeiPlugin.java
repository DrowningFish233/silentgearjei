package com.drowningfish233.silentgearjei.compat.jei;

import com.drowningfish233.silentgearjei.SilentGearJei;
import com.drowningfish233.silentgearjei.Utils.JEI.*;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearProperties;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@JeiPlugin
public class SilentGearTraitsJeiPlugin implements IModPlugin {
    public static final String MODID = SilentGearJei.MODID;
    private static final ResourceLocation PLUGIN_UID = ResourceLocation.tryParse(MODID + ":silentgear_traits");

    public static final RecipeType<Trait> TRAIT_TYPE =
            RecipeType.create(MODID, "silentgear_trait", Trait.class);

    private static final Map<ResourceLocation, List<Material>> TRAIT_TO_MATERIALS_CACHE = new ConcurrentHashMap<>();
    private static final Map<Material, List<ItemStack>> MATERIAL_DISPLAY_CACHE = new ConcurrentHashMap<>();
    private static List<GearType> ALL_GEAR_TYPES = null;
    private static List<PartType> ALL_PART_TYPES = null;

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new TraitCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        buildCache();

        List<Trait> allTraits = SilentGearJeiUtils.getAllTraitsSorted();
        registration.addRecipes(TRAIT_TYPE, allTraits);
    }

    /**
     * 构建缓存
     */
    private static void buildCache() {
        TRAIT_TO_MATERIALS_CACHE.clear();
        MATERIAL_DISPLAY_CACHE.clear();

        if (ALL_GEAR_TYPES == null) {
            ALL_GEAR_TYPES = new ArrayList<>();
            Registry<GearType> gearTypeRegistry = SgRegistries.GEAR_TYPE;
            for (GearType gearType : gearTypeRegistry) {
                ALL_GEAR_TYPES.add(gearType);
            }
        }

        // 初始化部件类型
        if (ALL_PART_TYPES == null) {
            ALL_PART_TYPES = new ArrayList<>();
            for (PartType partType : SgRegistries.PART_TYPE) {
                ALL_PART_TYPES.add(partType);
            }
        }

        var traitProperty = GearProperties.TRAITS.get();

        List<Material> allMaterials = SgRegistries.MATERIAL.getValues(true);

        for (Material material : allMaterials) {
            MaterialInstance instance = MaterialInstance.of(material);

            Set<ResourceLocation> traitsInMaterial = new HashSet<>();

            for (PartType partType : ALL_PART_TYPES) {
                for (GearType gearType : ALL_GEAR_TYPES) {
                    try {
                        var mods = instance.getPropertyModifiers(partType, PropertyKey.of(traitProperty, gearType));

                        for (var mod : mods) {
                            for (TraitInstance traitInstance : mod.value()) {
                                if (traitInstance.isValid()) {
                                    traitsInMaterial.add(traitInstance.getTraitId());
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }

            for (ResourceLocation traitId : traitsInMaterial) {
                TRAIT_TO_MATERIALS_CACHE
                        .computeIfAbsent(traitId, k -> new ArrayList<>())
                        .add(material);
            }

            computeMaterialDisplayStacks(material);
        }
    }

    /**
     * 计算并缓存材料的显示物品
     */
    private static List<ItemStack> computeMaterialDisplayStacks(Material material) {
        return MATERIAL_DISPLAY_CACHE.computeIfAbsent(material, m -> {
            List<ItemStack> stacks = new ArrayList<>();
            ItemStack displayStack = SilentGearJeiUtils.getMaterialDisplayStack(m);
            if (!displayStack.isEmpty()) {
                stacks.add(displayStack);
            } else {
                MaterialInstance materialInstance = MaterialInstance.of(m);
                Ingredient ingredient = materialInstance.getIngredient();
                ItemStack[] ingredientStacks = ingredient.getItems();
                for (ItemStack stack : ingredientStacks) {
                    if (!stack.isEmpty()) {
                        stacks.add(stack);
                    }
                }
            }
            return stacks;
        });
    }

    /**
     * 获取包含指定特质的所有材料
     */
    private static List<Material> getMaterialsWithTrait(Trait trait) {
        ResourceLocation traitId = SgRegistries.TRAIT.getKey(trait);
        List<Material> materials = TRAIT_TO_MATERIALS_CACHE.get(traitId);
        return materials != null ? materials : Collections.emptyList();
    }

    /**
     * 获取材料的显示物品
     */
    private static List<ItemStack> getMaterialDisplayStacks(Material material) {
        List<ItemStack> stacks = MATERIAL_DISPLAY_CACHE.get(material);
        return stacks != null ? stacks : Collections.emptyList();
    }

    public static class TraitCategory implements IRecipeCategory<Trait> {
        private final IDrawable background;
        private final IDrawable icon;
        private final IDrawable slotBackground;
        private final Component localizedName;

        private final int maxRows;
        private final int slotsPerRow;
        private final int maxSlots;

        public TraitCategory(IGuiHelper guiHelper) {
            this.background = guiHelper.createBlankDrawable(JeiLayoutConstants.SCROLL_AREA_WIDTH + 12,
                    JeiLayoutConstants.SCROLL_AREA_HEIGHT + 20);
            this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                    new ItemStack(Items.ENCHANTED_BOOK));
            this.slotBackground = guiHelper.getSlotDrawable();
            this.localizedName = Component.translatable(LocalizationKeys.JEI_TRAITS);

            this.slotsPerRow = JeiLayoutConstants.SLOTS_PER_ROW;
            this.maxRows = JeiLayoutConstants.ENABLE_DYNAMIC_ROW_CALCULATION ?
                    JeiLayoutConstants.MAX_MATERIAL_ROWS :
                    JeiLayoutConstants.FIXED_MAX_ROWS;
            this.maxSlots = this.slotsPerRow * this.maxRows;
        }

        @Override
        public RecipeType<Trait> getRecipeType() {
            return TRAIT_TYPE;
        }

        @Override
        public Component getTitle() {
            return localizedName;
        }

        @Override
        public IDrawable getIcon() {
            return icon;
        }

        @Override
        public IDrawable getBackground() {
            return background;
        }

        @Override
        public void setRecipe(IRecipeLayoutBuilder builder, Trait trait, IFocusGroup focuses) {
            builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                    .addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.ENCHANTED_BOOK));

            List<Material> allMaterials = SilentGearTraitsJeiPlugin.getMaterialsWithTrait(trait);

            if (allMaterials.isEmpty()) return;

            Map<Item, List<ItemStack>> groupedMaterials = new LinkedHashMap<>();

            for (Material material : allMaterials) {
                List<ItemStack> displayStacks = SilentGearTraitsJeiPlugin.getMaterialDisplayStacks(material);
                for (ItemStack stack : displayStacks) {
                    if (!stack.isEmpty()) {
                        groupedMaterials.computeIfAbsent(stack.getItem(), k -> new ArrayList<>())
                                .add(stack);
                    }
                }
            }

            List<List<ItemStack>> slotContents = new ArrayList<>();
            for (int i = 0; i < maxSlots; i++) {
                slotContents.add(new ArrayList<>());
            }

            int index = 0;
            for (Map.Entry<Item, List<ItemStack>> entry : groupedMaterials.entrySet()) {
                int slotIndex = index % maxSlots;
                slotContents.get(slotIndex).addAll(entry.getValue());
                index++;
            }

            int nonEmptySlots = 0;
            for (List<ItemStack> slot : slotContents) {
                if (!slot.isEmpty()) nonEmptySlots++;
            }
            int rowsNeeded = (int) Math.ceil((double) nonEmptySlots / slotsPerRow);
            int rowsToShow = Math.min(rowsNeeded, maxRows);

            for (int slotId = 0; slotId < maxSlots; slotId++) {
                List<ItemStack> stacksInSlot = slotContents.get(slotId);
                if (!stacksInSlot.isEmpty()) {
                    int row = slotId / slotsPerRow;
                    int col = slotId % slotsPerRow;

                    if (row < rowsToShow) {
                        int slotX = JeiLayoutConstants.MATERIAL_AREA_START_X + col * JeiLayoutConstants.SLOT_SIZE;
                        int slotY = JeiLayoutConstants.MATERIAL_AREA_START_Y + row * JeiLayoutConstants.SLOT_SIZE;

                        Set<ItemStack> uniqueStacks = new LinkedHashSet<>(stacksInSlot);
                        Ingredient ingredient = Ingredient.of(uniqueStacks.toArray(new ItemStack[0]));

                        builder.addSlot(RecipeIngredientRole.INPUT, slotX, slotY)
                                .addIngredients(ingredient)
                                .setBackground(slotBackground, -1, -1);
                    }
                }
            }
        }

        @Override
        public void draw(Trait trait, IRecipeSlotsView slotsView, GuiGraphics graphics,
                         double mouseX, double mouseY) {
            Font font = Minecraft.getInstance().font;

            int currentY = JeiLayoutConstants.TOP_PADDING;

            String name = SilentGearJeiUtils.getTraitDisplayName(trait).getString();
            MutableComponent nameComponent = Component.literal(name)
                    .withStyle(style -> style.withBold(true).withColor(TextColor.fromRgb(JeiLayoutConstants.TEXT_COLOR_PURPLE)));
            int nameWidth = font.width(nameComponent);
            int nameX = (JeiLayoutConstants.SCROLL_AREA_WIDTH + 12 - nameWidth) / 2;
            graphics.drawString(font, nameComponent.getVisualOrderText(), nameX, currentY,
                    JeiLayoutConstants.TEXT_COLOR_PURPLE, false);
            currentY += JeiLayoutConstants.TITLE_HEIGHT + 2;

            String id = JeiTextUtils.colored(LocalizationKeys.ID, "§7").getString() + " " +
                    SilentGearJeiUtils.getTraitId(trait);
            graphics.drawString(font, id, JeiLayoutConstants.TEXT_START_X, currentY,
                    JeiLayoutConstants.TEXT_COLOR_GRAY, false);
            currentY += JeiLayoutConstants.LINE_HEIGHT + JeiLayoutConstants.LINE_SPACING;

            String maxLevel = JeiTextUtils.colored(LocalizationKeys.MAX_LEVEL, "§7").getString() + " " +
                    trait.getMaxLevel();
            graphics.drawString(font, maxLevel, JeiLayoutConstants.TEXT_START_X, currentY,
                    JeiLayoutConstants.TEXT_COLOR_GRAY, false);
            currentY += JeiLayoutConstants.LINE_HEIGHT + JeiLayoutConstants.SECTION_SPACING;

            String descTitle = "§n" + JeiTextUtils.formatted(LocalizationKeys.DESCRIPTION).getString();
            graphics.drawString(font, descTitle, JeiLayoutConstants.TEXT_START_X, currentY,
                    JeiLayoutConstants.TEXT_COLOR_BLACK, false);
            currentY += JeiLayoutConstants.TITLE_HEIGHT + 2;

            String description = SilentGearJeiUtils.getTraitDescription(trait).getString();
            List<FormattedText> wrappedLines = font.getSplitter()
                    .splitLines(description, JeiLayoutConstants.SCROLL_AREA_WIDTH - 8, Style.EMPTY);

            for (FormattedText line : wrappedLines) {
                graphics.drawString(font, line.getString(), JeiLayoutConstants.TEXT_START_X, currentY,
                        JeiLayoutConstants.TEXT_COLOR_DARK_GRAY, false);
                currentY += JeiLayoutConstants.LINE_HEIGHT;
            }
        }
    }
}