package com.drowningfish233.silentgearjei.compat.jei;

import com.drowningfish233.silentgearjei.SilentGearJei;
import com.drowningfish233.silentgearjei.Utils.JEI.*;
import com.drowningfish233.silentgearjei.Utils.JEI.JeiLayoutConstants;
import com.drowningfish233.silentgearjei.Utils.JEI.JeiLineInfo;
import com.drowningfish233.silentgearjei.Utils.JEI.JeiWidgetFactory;
import com.drowningfish233.silentgearjei.Utils.JEI.LocalizationKeys;
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
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.PartTypes;

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

        /**
         * 判断值是否有效（普通属性）
         */
        private boolean hasValue(String value) {
            return value != null &&
                    !value.isEmpty() &&
                    !value.equals("0") &&
                    !value.equals("0.0") &&
                    !value.equals("0.00") &&
                    !value.equals("{}");
        }

        /**
         * 判断是否是采集等级属性
         */
        private boolean isHarvestTierProperty(GearProperty<?, ?> prop) {
            if (prop == null) return false;

            ResourceLocation registryKey = SgRegistries.GEAR_PROPERTY.getKey(prop);
            String propName = prop.getDisplayName().getString();
            String propKey = registryKey != null ? registryKey.getPath() : "";

            return propName.contains("Harvest Tier") ||
                    propName.contains("harvest_tier") ||
                    propKey.contains("harvest_tier") ||
                    propName.contains("Harvest Level") ||
                    propName.contains("采集等级") ||
                    propName.contains("挖掘等级");
        }

        /**
         * 格式化采集等级显示
         */
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


        /**
         * 判断属性是否应该显示
         */
        private boolean shouldShowProperty(GearProperty<?, ?> prop, String value) {
            if (value == null) return false;

            if (isHarvestTierProperty(prop)) {
                return !value.isEmpty() && !value.equals("{}");
            }

            // 其他属性按原有逻辑
            return hasValue(value);
        }

        /**
         * 获取属性显示值
         */
        private String getPropertyDisplayValue(GearProperty<?, ?> prop, String rawValue) {
            if (isHarvestTierProperty(prop)) {
                return formatHarvestTierValue(rawValue);
            }
            return rawValue;
        }

        /**
         * 获取属性显示名称
         */
        private String getPropertyDisplayName(GearProperty<?, ?> prop) {
            if (isHarvestTierProperty(prop)) {
                return Component.translatable("jei.reveriefoundry.harvest_level").getString();
            }
            return prop.getDisplayName().getString();
        }

        /**
         * 将带格式的文本按最大宽度换行，保留颜色
         */
        private List<String> wrapFormattedText(String text, int maxWidth) {
            Minecraft mc = Minecraft.getInstance();
            Font font = mc.font;

            String formatPrefix = extractFormattingPrefix(text);

            String plainText = ChatFormatting.stripFormatting(text);
            if (plainText == null) plainText = text;

            List<FormattedText> wrappedLines = font.getSplitter().splitLines(
                    plainText,
                    maxWidth,
                    Style.EMPTY
            );

            List<String> result = new ArrayList<>();
            for (int i = 0; i < wrappedLines.size(); i++) {
                String lineText = wrappedLines.get(i).getString();
                if (i == 0) {
                    result.add(formatPrefix + lineText);
                } else {
                    result.add(formatPrefix + "  " + lineText);
                }
            }

            return result;
        }

        /**
         * 提取文本开头的格式代码
         */
        private String extractFormattingPrefix(String text) {
            StringBuilder prefix = new StringBuilder();
            int i = 0;
            while (i < text.length() - 1) {
                if (text.charAt(i) == '§') {
                    prefix.append('§').append(text.charAt(i + 1));
                    i += 2;
                } else {
                    break;
                }
            }
            return prefix.toString();
        }

        /**
         * 创建带标签和值的行
         */
        private List<JeiLineInfo> createLabelValueLines(String label, String value, int startY, int indent) {
            List<JeiLineInfo> lines = new ArrayList<>();

            String indentStr = "  ".repeat(indent);
            String fullText = indentStr + JeiLayoutConstants.COLOR_LABEL + label + " " + JeiLayoutConstants.COLOR_VALUE + value;

            int availableWidth = JeiLayoutConstants.TEXT_MAX_WIDTH - (indent * 2 * 4);

            List<String> wrappedLines = wrapFormattedText(fullText, availableWidth);

            int currentY = startY;
            for (String line : wrappedLines) {
                boolean isBold = line.contains("§l");
                lines.add(new JeiLineInfo(currentY, line, isBold));
                currentY += JeiLayoutConstants.LINE_HEIGHT;
            }

            return lines;
        }

        /**
         * 创建属性行
         */
        private List<JeiLineInfo> createPropertyLine(String propName, String value, int startY, int indent) {
            List<JeiLineInfo> lines = new ArrayList<>();

            String indentStr = "  ".repeat(indent);
            String fullText = indentStr + JeiLayoutConstants.COLOR_PROP_NAME + propName + ": " + JeiLayoutConstants.COLOR_VALUE + value;

            int availableWidth = JeiLayoutConstants.TEXT_MAX_WIDTH - (indent * 2 * 4);
            List<String> wrappedLines = wrapFormattedText(fullText, availableWidth);

            int currentY = startY;
            for (String line : wrappedLines) {
                boolean isBold = line.contains("§l");
                lines.add(new JeiLineInfo(currentY, line, isBold));
                currentY += JeiLayoutConstants.LINE_HEIGHT;
            }

            return lines;
        }

        /**
         * 创建标题行
         */
        private List<JeiLineInfo> createTitleLine(String title, int startY) {
            List<JeiLineInfo> lines = new ArrayList<>();

            String fullText = JeiLayoutConstants.COLOR_TITLE + title;
            List<String> wrappedLines = wrapFormattedText(fullText, JeiLayoutConstants.TEXT_MAX_WIDTH);

            int currentY = startY;
            for (String line : wrappedLines) {
                lines.add(new JeiLineInfo(currentY, line, true));
                currentY += JeiLayoutConstants.LINE_HEIGHT;
            }

            return lines;
        }

        /**
         * 创建小节标题行
         */
        private List<JeiLineInfo> createSectionTitleLine(String title, int startY, int indent) {
            List<JeiLineInfo> lines = new ArrayList<>();

            String indentStr = "  ".repeat(indent);
            String fullText = indentStr + JeiLayoutConstants.COLOR_SUBTITLE + title;

            int availableWidth = JeiLayoutConstants.TEXT_MAX_WIDTH - (indent * 2 * 4);
            List<String> wrappedLines = wrapFormattedText(fullText, availableWidth);

            int currentY = startY;
            for (String line : wrappedLines) {
                lines.add(new JeiLineInfo(currentY, line, false));
                currentY += JeiLayoutConstants.LINE_HEIGHT;
            }

            return lines;
        }

        /**
         * 构建所有显示内容
         */
        private List<JeiLineInfo> buildAllContent(Material material) {
            List<JeiLineInfo> allLines = new ArrayList<>();
            int currentY = JeiLayoutConstants.TOP_PADDING;

            String materialName = SilentGearJeiUtils.getMaterialDisplayName(material, PartTypes.MAIN.get()).getString();
            allLines.addAll(createTitleLine(materialName, currentY));
            currentY += allLines.size() > 0 ?
                    (allLines.get(allLines.size() - 1).y - currentY + JeiLayoutConstants.LINE_HEIGHT) : JeiLayoutConstants.LINE_HEIGHT;

            ResourceLocation idResource = SilentGearJeiUtils.getMaterialId(material);
            String id = idResource != null ? idResource.toString() : Component.translatable(LocalizationKeys.UNKNOWN).getString();
            allLines.addAll(createLabelValueLines(Component.translatable(LocalizationKeys.ID).getString(), id, currentY, 0));
            currentY += allLines.size() > 0 ?
                    (allLines.get(allLines.size() - 1).y - currentY + JeiLayoutConstants.LINE_HEIGHT) : JeiLayoutConstants.LINE_HEIGHT;

            Material parent = SilentGearJeiUtils.getMaterialParent(material);
            if (parent != null) {
                String parentName = SilentGearJeiUtils.getMaterialDisplayName(parent, PartTypes.MAIN.get()).getString();
                allLines.addAll(createLabelValueLines(Component.translatable(LocalizationKeys.PARENT).getString(), parentName, currentY, 0));
                currentY += allLines.size() > 0 ?
                        (allLines.get(allLines.size() - 1).y - currentY + JeiLayoutConstants.LINE_HEIGHT) : JeiLayoutConstants.LINE_HEIGHT;
            }

            String type = material.isSimple() ?
                    Component.translatable(LocalizationKeys.TYPE_SIMPLE).getString() :
                    Component.translatable(LocalizationKeys.TYPE_COMPOUND).getString();
            allLines.addAll(createLabelValueLines(Component.translatable(LocalizationKeys.TYPE).getString(), type, currentY, 0));
            currentY += allLines.size() > 0 ?
                    (allLines.get(allLines.size() - 1).y - currentY + JeiLayoutConstants.LINE_HEIGHT) : JeiLayoutConstants.LINE_HEIGHT;

            String source = SilentGearJeiUtils.getMaterialPackName(material);
            allLines.addAll(createLabelValueLines(Component.translatable(LocalizationKeys.SOURCE).getString(), source, currentY, 0));
            currentY += allLines.size() > 0 ?
                    (allLines.get(allLines.size() - 1).y - currentY + JeiLayoutConstants.LINE_HEIGHT) : JeiLayoutConstants.LINE_HEIGHT;

            List<String> categories = SilentGearJeiUtils.getMaterialCategories(material);
            if (!categories.isEmpty()) {
                String categoriesStr = String.join(", ", categories);
                allLines.addAll(createLabelValueLines(Component.translatable(LocalizationKeys.CATEGORIES).getString(), categoriesStr, currentY, 0));
                currentY += allLines.size() > 0 ?
                        (allLines.get(allLines.size() - 1).y - currentY + JeiLayoutConstants.LINE_HEIGHT) : JeiLayoutConstants.LINE_HEIGHT;
            }

            List<PartType> allowedParts = SilentGearJeiUtils.getAllowedPartTypes(material);
            if (!allowedParts.isEmpty()) {
                String partTypesStr = allowedParts.stream()
                        .map(pt -> pt.getDisplayName().getString())
                        .collect(Collectors.joining(", "));
                allLines.addAll(createLabelValueLines(Component.translatable(LocalizationKeys.ALLOWED_PARTS).getString(), partTypesStr, currentY, 0));
                currentY += allLines.size() > 0 ?
                        (allLines.get(allLines.size() - 1).y - currentY + JeiLayoutConstants.LINE_HEIGHT) : JeiLayoutConstants.LINE_HEIGHT;
            }

            currentY += JeiLayoutConstants.SECTION_SPACING;

            allLines.addAll(createSectionTitleLine(Component.translatable(LocalizationKeys.PROPERTIES).getString(), currentY, 0));
            currentY += allLines.size() > 0 ?
                    (allLines.get(allLines.size() - 1).y - currentY + JeiLayoutConstants.LINE_HEIGHT) : JeiLayoutConstants.LINE_HEIGHT;

            List<PartType> partTypes = SilentGearJeiUtils.getAllowedPartTypes(material);
            List<GearProperty<?, ?>> properties = SilentGearJeiUtils.getAllProperties();

            boolean hasAnyProperty = false;

            for (PartType partType : partTypes) {
                boolean hasPartProperties = false;
                List<JeiLineInfo> partLines = new ArrayList<>();

                for (GearProperty<?, ?> prop : properties) {
                    String rawValue = SilentGearJeiUtils.getMaterialPropertyValue(material, partType, prop);

                    if (shouldShowProperty(prop, rawValue)) {
                        if (!hasPartProperties) {
                            String partName = partType.getDisplayName().getString();
                            partLines.addAll(createSectionTitleLine(partName + ":", currentY, 1));
                            currentY += partLines.size() > 0 ?
                                    (partLines.get(partLines.size() - 1).y - currentY + JeiLayoutConstants.LINE_HEIGHT) : JeiLayoutConstants.LINE_HEIGHT;
                            hasPartProperties = true;
                            hasAnyProperty = true;
                        }

                        String propName = getPropertyDisplayName(prop);
                        String displayValue = getPropertyDisplayValue(prop, rawValue);

                        partLines.addAll(createPropertyLine(propName, displayValue, currentY, 2));
                        currentY += partLines.size() > 0 ?
                                (partLines.get(partLines.size() - 1).y - currentY + JeiLayoutConstants.LINE_HEIGHT) : JeiLayoutConstants.LINE_HEIGHT;
                    }
                }

                allLines.addAll(partLines);

                if (hasPartProperties) {
                    currentY += 2;
                }
            }

            if (!hasAnyProperty) {
                String noPropsText = JeiLayoutConstants.COLOR_VALUE + Component.translatable(LocalizationKeys.NO_PROPERTIES).getString();
                List<String> noPropsWrapped = wrapFormattedText("  " + noPropsText, JeiLayoutConstants.TEXT_MAX_WIDTH - 20);
                for (String line : noPropsWrapped) {
                    allLines.add(new JeiLineInfo(currentY, line, false));
                    currentY += JeiLayoutConstants.LINE_HEIGHT;
                }
            }

            return allLines;
        }

        @Override
        public void createRecipeExtras(IRecipeExtrasBuilder builder, Material material, IFocusGroup focuses) {
            List<JeiLineInfo> contentLines = buildAllContent(material);

            if (!contentLines.isEmpty()) {
                int contentHeight = 0;
                if (!contentLines.isEmpty()) {
                    JeiLineInfo lastLine = contentLines.get(contentLines.size() - 1);
                    contentHeight = lastLine.y + JeiLayoutConstants.LINE_HEIGHT + 10;
                }

                List<IRecipeWidget> lineWidgets = new ArrayList<>();

                for (JeiLineInfo line : contentLines) {
                    lineWidgets.add(JeiWidgetFactory.createTextWidget(
                            line.text,
                            JeiLayoutConstants.TEXT_START_X,
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