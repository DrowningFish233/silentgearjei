package com.drowningfish233.silentgearjei.Utils.JEI;

import com.drowningfish233.silentgearjei.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class JeiLayoutConstants {
    private static final Logger LOGGER = LogManager.getLogger();

    // 图标相关
    public static int ICON_SIZE = 18;
    public static int ICON_X = 1;
    public static int ICON_Y = 1;
    public static int TEXT_START_X = 23;

    // 文本相关
    public static int TOP_PADDING = 4;
    public static int LINE_HEIGHT = 9;
    public static int LINE_SPACING = 2;
    public static int INDENT = 10;
    public static int TITLE_HEIGHT = 12;
    public static int SECTION_SPACING = 4;
    public static int TEXT_MAX_WIDTH = 140;

    // 滚动区域
    public static int SCROLL_AREA_X = 0;
    public static int SCROLL_AREA_Y = 3;
    public static int SCROLL_AREA_WIDTH = 164;
    public static int SCROLL_AREA_HEIGHT = 180;

    // 槽位布局
    public static int MATERIAL_AREA_START_X = 1;
    public static int MATERIAL_AREA_START_Y = 100;
    public static int SLOT_SIZE = 18;
    public static int SLOTS_PER_ROW = 9;

    // 新增：行数限制配置
    public static int MAX_MATERIAL_ROWS = 3;
    public static int FIXED_MAX_ROWS = 3;
    public static boolean ENABLE_DYNAMIC_ROW_CALCULATION = true;

    // 文本限制
    public static int MAX_PARTS_PER_LINE = 3;
    public static int MAX_TRAIT_LINE_WIDTH = 35;
    public static int TOOLTIP_WIDTH = 35;

    // 颜色常量
    public static int TEXT_COLOR_GRAY = 0xFF888888;
    public static int TEXT_COLOR_PURPLE = 0xFFAA00AA;
    public static int TEXT_COLOR_BLACK = 0xFF000000;
    public static int TEXT_COLOR_DARK_GRAY = 0xFF555555;
    public static String COLOR_LABEL = "§3";
    public static String COLOR_VALUE = "§f";
    public static String COLOR_TITLE = "§6§l";
    public static String COLOR_SUBTITLE = "§b§n";
    public static String COLOR_PROP_NAME = "§7";

    private JeiLayoutConstants() {}

    private static int parseColor(String colorStr, int defaultValue) {
        try {
            if (colorStr.startsWith("0x") || colorStr.startsWith("0X")) {
                return Integer.parseUnsignedInt(colorStr.substring(2), 16);
            } else if (colorStr.startsWith("#")) {
                return Integer.parseUnsignedInt(colorStr.substring(1), 16);
            } else {
                return Integer.parseUnsignedInt(colorStr, 16);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to parse color: {}, using default: {}", colorStr, defaultValue);
            return defaultValue;
        }
    }

    public static void init() {
        Config.Client config = Config.CLIENT;

        // 图标相关
        ICON_SIZE = config.iconSize.get();
        ICON_X = config.iconX.get();
        ICON_Y = config.iconY.get();
        TEXT_START_X = config.textStartX.get();

        // 文本相关
        TOP_PADDING = config.topPadding.get();
        LINE_HEIGHT = config.lineHeight.get();
        LINE_SPACING = config.lineSpacing.get();
        INDENT = config.indent.get();
        TITLE_HEIGHT = config.titleHeight.get();
        SECTION_SPACING = config.sectionSpacing.get();
        TEXT_MAX_WIDTH = config.textMaxWidth.get();

        // 滚动区域
        SCROLL_AREA_X = config.scrollAreaX.get();
        SCROLL_AREA_Y = config.scrollAreaY.get();
        SCROLL_AREA_WIDTH = config.scrollAreaWidth.get();
        SCROLL_AREA_HEIGHT = config.scrollAreaHeight.get();

        // 槽位布局
        MATERIAL_AREA_START_X = config.materialAreaStartX.get();
        MATERIAL_AREA_START_Y = config.materialAreaStartY.get();
        SLOT_SIZE = config.slotSize.get();
        SLOTS_PER_ROW = config.slotsPerRow.get();

        // 行数限制配置
        MAX_MATERIAL_ROWS = config.maxMaterialRows.get();
        FIXED_MAX_ROWS = config.fixedMaxRows.get();
        ENABLE_DYNAMIC_ROW_CALCULATION = config.enableDynamicRowCalculation.get();

        // 文本限制
        MAX_PARTS_PER_LINE = config.maxPartsPerLine.get();
        MAX_TRAIT_LINE_WIDTH = config.maxTraitLineWidth.get();
        TOOLTIP_WIDTH = config.tooltipWidth.get();

        // 颜色配置
        TEXT_COLOR_GRAY = parseColor(config.textColorGray.get(), 0xFF888888);
        TEXT_COLOR_PURPLE = parseColor(config.textColorPurple.get(), 0xFFAA00AA);
        TEXT_COLOR_BLACK = parseColor(config.textColorBlack.get(), 0xFF000000);
        TEXT_COLOR_DARK_GRAY = parseColor(config.textColorDarkGray.get(), 0xFF555555);
        COLOR_LABEL = config.textColorLabel.get();
        COLOR_VALUE = config.textColorValue.get();
        COLOR_TITLE = config.textColorTitle.get();
        COLOR_SUBTITLE = config.textColorSubtitle.get();
        COLOR_PROP_NAME = config.textColorPropName.get();
    }
}