package com.drowningfish233.silentgearjei.compat.jei;

import com.drowningfish233.silentgearjei.SilentGearJei;
import com.drowningfish233.silentgearjei.Utils.JEI.*;
import com.yanny.aci.api.Rect;
import com.yanny.ali.jei.compatibility.jei.JeiScrollWidget;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.PartTypes;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@JeiPlugin
public class SilentGearMaterialsJeiPlugin implements IModPlugin {
    public static final String MODID = SilentGearJei.MODID;
    private static final ResourceLocation PLUGIN_UID = ResourceLocation.tryParse(MODID + ":silentgear_materials");

    public static final RecipeType<Material> MATERIAL_TYPE =
            RecipeType.create(MODID, "silentgear_material", Material.class);

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new MaterialCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<Material> recipes = SilentGearJeiUtils.getAllMaterialsSorted();
        registration.addRecipes(MATERIAL_TYPE, recipes);
    }

    public static class MaterialCategory implements IRecipeCategory<Material> {
        private final IDrawable background;
        private final IDrawable icon;
        private final Component localizedName;

        private static final int LINE_HEIGHT = 11;
        private static final int TITLE_LINE_HEIGHT = 14;
        private static final int SECTION_SPACING = 4;
        private static final int CONTENT_X = 23;
        private static final int TEXT_MAX_WIDTH = 155;

        public MaterialCategory(IGuiHelper guiHelper) {
            this.background = guiHelper.createBlankDrawable(
                    JeiLayoutConstants.SCROLL_AREA_WIDTH + 6,
                    JeiLayoutConstants.SCROLL_AREA_HEIGHT + 20
            );
            this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                    new ItemStack(Items.IRON_INGOT));
            this.localizedName = Component.translatable(LocalizationKeys.JEI_MATERIALS);
        }

        @Override
        public RecipeType<Material> getRecipeType() {
            return MATERIAL_TYPE;
        }

        @Override
        public Component getTitle() {
            return localizedName;
        }

        @Override
        public IDrawable getBackground() {
            return background;
        }

        @Override
        public IDrawable getIcon() {
            return icon;
        }

        @Override
        public void setRecipe(IRecipeLayoutBuilder builder, Material material, IFocusGroup focuses) {
            MaterialInstance materialInstance = MaterialInstance.of(material);

            List<ItemStack> displayStacks = new ArrayList<>();

            ItemStack displayStack = SilentGearJeiUtils.getMaterialDisplayStack(material);
            if (!displayStack.isEmpty()) {
                displayStacks.add(displayStack);
            }

            Ingredient ingredient = materialInstance.getIngredient();
            for (ItemStack stack : ingredient.getItems()) {
                if (!stack.isEmpty() && !displayStacks.contains(stack)) {
                    displayStacks.add(stack);
                }
            }

            if (!displayStacks.isEmpty()) {
                Ingredient combinedIngredient = Ingredient.of(displayStacks.toArray(new ItemStack[0]));
                builder.addSlot(RecipeIngredientRole.INPUT, JeiLayoutConstants.ICON_X, JeiLayoutConstants.ICON_Y)
                        .setStandardSlotBackground()
                        .addIngredients(combinedIngredient);
            }
        }

        private boolean hasValue(String value) {
            return value != null &&
                    !value.isEmpty() &&
                    !value.equals("0") &&
                    !value.equals("0.0") &&
                    !value.equals("0.00") &&
                    !value.equals("{}");
        }

        private boolean isHarvestTierProperty(GearProperty<?, ?> prop) {
            if (prop == null) return false;

            ResourceLocation registryKey = SgRegistries.GEAR_PROPERTY.getKey(prop);
            if (registryKey == null) return false;

            String path = registryKey.getPath();
            return path.equals("harvest_tier") || path.equals("harvest_level");
        }

        private String formatHarvestTierValue(String rawValue) {
            if (rawValue == null || rawValue.isEmpty() || rawValue.equals("{}")) {
                return "0";
            }

            try {
                Pattern levelHintPattern = Pattern.compile("level_hint\"\\s*:\\s*\"(\\d+)\"");
                java.util.regex.Matcher matcher = levelHintPattern.matcher(rawValue);
                if (matcher.find()) {
                    return matcher.group(1);
                }

                Pattern numberPattern = Pattern.compile("(\\d+)");
                matcher = numberPattern.matcher(rawValue);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            } catch (Exception e) {
            }

            return "0";
        }

        private boolean shouldShowProperty(GearProperty<?, ?> prop, String value) {
            if (value == null) return false;

            if (isHarvestTierProperty(prop)) {
                return !value.isEmpty() && !value.equals("{}");
            }

            return hasValue(value);
        }

        private String getPropertyDisplayValue(GearProperty<?, ?> prop, String rawValue) {
            if (isHarvestTierProperty(prop)) {
                return formatHarvestTierValue(rawValue);
            }
            return rawValue;
        }

        private String getPropertyDisplayName(GearProperty<?, ?> prop) {
            if (isHarvestTierProperty(prop)) {
                return Component.translatable("jei.reveriefoundry.harvest_level").getString();
            }
            return prop.getDisplayName().getString();
        }

        private String getColoredValueString(String value) {
            try {
                double num = Double.parseDouble(value);
                String formatted = DECIMAL_FORMAT.format(num);
                if (num > 0) {
                    return "§a+" + formatted;
                } else if (num < 0) {
                    return "§c" + formatted;
                } else {
                    return "§f" + formatted;
                }
            } catch (NumberFormatException ignored) {
                return "§f" + value;
            }
        }

        private List<String> wrapPlainText(String text, int maxWidth) {
            Minecraft mc = Minecraft.getInstance();
            Font font = mc.font;

            List<FormattedText> wrappedLines = font.getSplitter().splitLines(
                    text,
                    maxWidth,
                    Style.EMPTY
            );

            List<String> result = new ArrayList<>();
            for (FormattedText line : wrappedLines) {
                result.add(line.getString());
            }
            return result;
        }

        private List<String> wrapTextWithPrefix(String text, int maxWidth, String colorPrefix) {
            Minecraft mc = Minecraft.getInstance();
            Font font = mc.font;

            String plainText = ChatFormatting.stripFormatting(text);

            List<FormattedText> wrappedLines = font.getSplitter().splitLines(
                    plainText,
                    maxWidth,
                    Style.EMPTY
            );

            List<String> result = new ArrayList<>();
            for (int i = 0; i < wrappedLines.size(); i++) {
                String lineText = wrappedLines.get(i).getString();
                if (i == 0) {
                    result.add(colorPrefix + lineText);
                } else {
                    result.add(colorPrefix + "  " + lineText);
                }
            }
            return result;
        }

        private List<JeiLineInfo> createLabelValueLines(String label, String value, int startY) {
            List<JeiLineInfo> lines = new ArrayList<>();

            String fullText = label + " " + value;
            String colorPrefix = "§3";

            List<String> wrappedLines = wrapTextWithPrefix(fullText, TEXT_MAX_WIDTH, colorPrefix);

            int currentY = startY;
            for (String line : wrappedLines) {
                lines.add(new JeiLineInfo(currentY, line, false));
                currentY += LINE_HEIGHT;
            }

            return lines;
        }

        /**
         * 属性行 - 属性名和值分开处理，值单独带颜色
         */
        private List<JeiLineInfo> createPropertyLine(String propName, String value, int startY) {
            List<JeiLineInfo> lines = new ArrayList<>();

            String coloredValue = getColoredValueString(value);
            String fullText = propName + ": " + coloredValue;
            String colorPrefix = "  §0";

            List<String> wrappedLines = wrapTextWithPrefix(fullText, TEXT_MAX_WIDTH, colorPrefix);

            int currentY = startY;
            for (String line : wrappedLines) {
                lines.add(new JeiLineInfo(currentY, line, false));
                currentY += LINE_HEIGHT;
            }

            return lines;
        }

        /**
         * 属性行改进版 - 分开绘制属性名和值，确保值颜色不丢失
         * 返回两个 JeiLineInfo，一个用于属性名，一个用于值
         */
        private List<JeiLineInfo> createPropertyLineSeparate(String propName, String value, int startY) {
            List<JeiLineInfo> lines = new ArrayList<>();

            String coloredValue = getColoredValueString(value);
            String prefixText = "  §0" + propName + ": ";

            Minecraft mc = Minecraft.getInstance();
            Font font = mc.font;
            int prefixWidth = font.width(ChatFormatting.stripFormatting(prefixText));

            String fullPlain = propName + ": " + ChatFormatting.stripFormatting(value);
            if (font.width(fullPlain) <= TEXT_MAX_WIDTH) {
                lines.add(new JeiLineInfo(startY, prefixText + coloredValue, false));
                return lines;
            }

            List<String> propWrapped = wrapPlainText(prefixText, TEXT_MAX_WIDTH);
            for (String line : propWrapped) {
                lines.add(new JeiLineInfo(startY, line, false));
                startY += LINE_HEIGHT;
            }

            String valueIndent = "    ";
            List<String> valueWrapped = wrapPlainText(coloredValue, TEXT_MAX_WIDTH - font.width(valueIndent));
            for (String line : valueWrapped) {
                lines.add(new JeiLineInfo(startY, valueIndent + line, false));
                startY += LINE_HEIGHT;
            }

            return lines;
        }

        private List<JeiLineInfo> createTitleLine(String title, int startY) {
            List<JeiLineInfo> lines = new ArrayList<>();

            String colorPrefix = "§6§l";
            List<String> wrappedLines = wrapTextWithPrefix(title, TEXT_MAX_WIDTH, colorPrefix);

            int currentY = startY;
            for (String line : wrappedLines) {
                lines.add(new JeiLineInfo(currentY, line, true));
                currentY += TITLE_LINE_HEIGHT;
            }

            return lines;
        }

        private List<JeiLineInfo> createSectionTitleLine(String title, int startY) {
            List<JeiLineInfo> lines = new ArrayList<>();

            String colorPrefix = "§b■ ";
            List<String> wrappedLines = wrapTextWithPrefix(title, TEXT_MAX_WIDTH, colorPrefix);

            int currentY = startY;
            for (String line : wrappedLines) {
                lines.add(new JeiLineInfo(currentY, line, false));
                currentY += LINE_HEIGHT;
            }

            return lines;
        }

        private List<JeiLineInfo> buildAllContent(Material material) {
            List<JeiLineInfo> allLines = new ArrayList<>();
            int currentY = 4;

            // 材料名称
            String materialName = SilentGearJeiUtils.getMaterialDisplayName(material, PartTypes.MAIN.get()).getString();
            allLines.addAll(createTitleLine(materialName, currentY));
            currentY += !allLines.isEmpty() ?
                    (allLines.get(allLines.size() - 1).y - currentY + TITLE_LINE_HEIGHT) : TITLE_LINE_HEIGHT;

            // ID
            ResourceLocation idResource = SilentGearJeiUtils.getMaterialId(material);
            String id = idResource != null ? idResource.toString() : Component.translatable(LocalizationKeys.UNKNOWN).getString();
            allLines.addAll(createLabelValueLines(Component.translatable(LocalizationKeys.ID).getString(), id, currentY));
            currentY += !allLines.isEmpty() ?
                    (allLines.get(allLines.size() - 1).y - currentY + LINE_HEIGHT) : LINE_HEIGHT;

            // 父材料
            Material parent = SilentGearJeiUtils.getMaterialParent(material);
            if (parent != null) {
                String parentName = SilentGearJeiUtils.getMaterialDisplayName(parent, PartTypes.MAIN.get()).getString();
                allLines.addAll(createLabelValueLines(Component.translatable(LocalizationKeys.PARENT).getString(), parentName, currentY));
                currentY += !allLines.isEmpty() ?
                        (allLines.get(allLines.size() - 1).y - currentY + LINE_HEIGHT) : LINE_HEIGHT;
            }

            // 类型
            String type = material.isSimple() ?
                    Component.translatable(LocalizationKeys.TYPE_SIMPLE).getString() :
                    Component.translatable(LocalizationKeys.TYPE_COMPOUND).getString();
            allLines.addAll(createLabelValueLines(Component.translatable(LocalizationKeys.TYPE).getString(), type, currentY));
            currentY += !allLines.isEmpty() ?
                    (allLines.get(allLines.size() - 1).y - currentY + LINE_HEIGHT) : LINE_HEIGHT;

            // 来源
            String source = SilentGearJeiUtils.getMaterialPackName(material);
            allLines.addAll(createLabelValueLines(Component.translatable(LocalizationKeys.SOURCE).getString(), source, currentY));
            currentY += !allLines.isEmpty() ?
                    (allLines.get(allLines.size() - 1).y - currentY + LINE_HEIGHT) : LINE_HEIGHT;

            // 分类
            List<String> categories = SilentGearJeiUtils.getMaterialCategories(material);
            if (!categories.isEmpty()) {
                String categoriesStr = String.join(", ", categories);
                allLines.addAll(createLabelValueLines(Component.translatable(LocalizationKeys.CATEGORIES).getString(), categoriesStr, currentY));
                currentY += !allLines.isEmpty() ?
                        (allLines.get(allLines.size() - 1).y - currentY + LINE_HEIGHT) : LINE_HEIGHT;
            }

            currentY += SECTION_SPACING;

            // 处理各个部件的属性
            List<PartType> partTypes = SilentGearJeiUtils.getAllowedPartTypes(material);
            List<GearProperty<?, ?>> properties = SilentGearJeiUtils.getAllProperties();

            boolean hasAnyProperty = false;

            for (PartType partType : partTypes) {
                boolean hasPartProperties = false;

                for (GearProperty<?, ?> prop : properties) {
                    String rawValue = SilentGearJeiUtils.getMaterialPropertyValue(material, partType, prop);

                    if (shouldShowProperty(prop, rawValue)) {
                        if (!hasPartProperties) {
                            String partName = partType.getDisplayName().getString();
                            allLines.addAll(createSectionTitleLine(partName, currentY));
                            currentY += !allLines.isEmpty() ?
                                    (allLines.get(allLines.size() - 1).y - currentY + LINE_HEIGHT) : LINE_HEIGHT;
                            hasPartProperties = true;
                            hasAnyProperty = true;
                        }

                        String propName = getPropertyDisplayName(prop);
                        String displayValue = getPropertyDisplayValue(prop, rawValue);

                        // 使用改进的分离绘制方法
                        allLines.addAll(createPropertyLineSeparate(propName, displayValue, currentY));
                        currentY += !allLines.isEmpty() ?
                                (allLines.get(allLines.size() - 1).y - currentY + LINE_HEIGHT) : LINE_HEIGHT;
                    }
                }

                if (hasPartProperties) {
                    currentY += 2;
                }
            }

            if (!hasAnyProperty) {
                String noPropsText = Component.translatable(LocalizationKeys.NO_PROPERTIES).getString();
                String colorPrefix = "  §0";
                List<String> noPropsWrapped = wrapTextWithPrefix(noPropsText, TEXT_MAX_WIDTH, colorPrefix);
                for (String line : noPropsWrapped) {
                    allLines.add(new JeiLineInfo(currentY, line, false));
                    currentY += LINE_HEIGHT;
                }
            }

            return allLines;
        }

        @Override
        public void createRecipeExtras(IRecipeExtrasBuilder builder, Material material, IFocusGroup focuses) {
            List<JeiLineInfo> contentLines = buildAllContent(material);

            if (!contentLines.isEmpty()) {
                int contentHeight = 0;
                JeiLineInfo lastLine = contentLines.get(contentLines.size() - 1);
                contentHeight = lastLine.y + LINE_HEIGHT + 10;

                List<IRecipeWidget> lineWidgets = new ArrayList<>();

                for (JeiLineInfo line : contentLines) {
                    lineWidgets.add(JeiWidgetFactory.createTextWidget(
                            line.text,
                            CONTENT_X,
                            line.y,
                            line.isBold
                    ));
                }

                JeiScrollWidget mainScrollWidget = new JeiScrollWidget(
                        new Rect(JeiLayoutConstants.SCROLL_AREA_X,
                                JeiLayoutConstants.SCROLL_AREA_Y,
                                JeiLayoutConstants.SCROLL_AREA_WIDTH + JeiScrollWidget.getScrollbarExtraWidth(),
                                JeiLayoutConstants.SCROLL_AREA_HEIGHT),
                        contentHeight,
                        lineWidgets
                );

                builder.addWidget(mainScrollWidget);
                builder.addInputHandler(mainScrollWidget);
            } else {
                builder.addText(Component.translatable(LocalizationKeys.NO_INFO),
                                JeiLayoutConstants.SCROLL_AREA_WIDTH, JeiLayoutConstants.LINE_HEIGHT)
                        .setPosition(JeiLayoutConstants.SCROLL_AREA_X, JeiLayoutConstants.SCROLL_AREA_Y)
                        .setColor(0xFF888888);
            }
        }
    }
}