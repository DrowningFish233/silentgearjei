package com.drowningfish233.silentgearjei;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
    public static class Client {
        // 图标相关
        public final ModConfigSpec.IntValue iconSize;
        public final ModConfigSpec.IntValue iconX;
        public final ModConfigSpec.IntValue iconY;
        public final ModConfigSpec.IntValue textStartX;

        public final ModConfigSpec.IntValue maxMaterialRows;
        public final ModConfigSpec.IntValue fixedMaxRows;
        public final ModConfigSpec.BooleanValue enableDynamicRowCalculation;

        // 文本相关
        public final ModConfigSpec.IntValue topPadding;
        public final ModConfigSpec.IntValue lineHeight;
        public final ModConfigSpec.IntValue lineSpacing;
        public final ModConfigSpec.IntValue indent;
        public final ModConfigSpec.IntValue titleHeight;
        public final ModConfigSpec.IntValue sectionSpacing;

        // 滚动区域
        public final ModConfigSpec.IntValue scrollAreaX;
        public final ModConfigSpec.IntValue scrollAreaY;
        public final ModConfigSpec.IntValue scrollAreaWidth;
        public final ModConfigSpec.IntValue scrollAreaHeight;

        // 槽位布局
        public final ModConfigSpec.IntValue materialAreaStartX;
        public final ModConfigSpec.IntValue materialAreaStartY;
        public final ModConfigSpec.IntValue slotSize;
        public final ModConfigSpec.IntValue slotsPerRow;

        // 文本限制
        public final ModConfigSpec.IntValue maxPartsPerLine;
        public final ModConfigSpec.IntValue maxTraitLineWidth;
        public final ModConfigSpec.IntValue tooltipWidth;
        public final ModConfigSpec.IntValue textMaxWidth;

        // 颜色配置
        public final ModConfigSpec.ConfigValue<String> textColorGray;
        public final ModConfigSpec.ConfigValue<String> textColorPurple;
        public final ModConfigSpec.ConfigValue<String> textColorBlack;
        public final ModConfigSpec.ConfigValue<String> textColorDarkGray;
        public final ModConfigSpec.ConfigValue<String> textColorLabel;
        public final ModConfigSpec.ConfigValue<String> textColorValue;
        public final ModConfigSpec.ConfigValue<String> textColorTitle;
        public final ModConfigSpec.ConfigValue<String> textColorSubtitle;
        public final ModConfigSpec.ConfigValue<String> textColorPropName;

        public final ModConfigSpec.BooleanValue enableSearchTerms;
        public final ModConfigSpec.BooleanValue requireAdvancedTooltips;
        public final ModConfigSpec.BooleanValue enableTraitSearch;
        public final ModConfigSpec.BooleanValue enablePartTypeSearch;

        public Client(ModConfigSpec.Builder builder) {
            builder.comment("Silent Gear Jei -JEI Layout Configuration").push("jei_layout");

            // 图标相关
            builder.comment("Icon Settings").push("icon");
            iconSize = builder
                    .comment("Size of the icon in pixels")
                    .defineInRange("iconSize", 18, 8, 32);
            iconX = builder
                    .comment("X position of the icon")
                    .defineInRange("iconX", 1, -10, 50);
            iconY = builder
                    .comment("Y position of the icon")
                    .defineInRange("iconY", 1, -10, 50);
            textStartX = builder
                    .comment("X position where text starts")
                    .defineInRange("textStartX", 23, 0, 100);
            builder.pop();

            // 文本相关
            builder.comment("Text Settings").push("text");
            topPadding = builder
                    .comment("Top padding for text")
                    .defineInRange("topPadding", 4, 0, 50);
            lineHeight = builder
                    .comment("Height of each text line")
                    .defineInRange("lineHeight", 9, 5, 30);
            lineSpacing = builder
                    .comment("Spacing between lines")
                    .defineInRange("lineSpacing", 2, 0, 20);
            indent = builder
                    .comment("Indentation for nested text")
                    .defineInRange("indent", 10, 0, 50);
            titleHeight = builder
                    .comment("Height of title text")
                    .defineInRange("titleHeight", 12, 5, 30);
            sectionSpacing = builder
                    .comment("Spacing between sections")
                    .defineInRange("sectionSpacing", 4, 0, 20);
            builder.pop();

            // 滚动区域
            builder.comment("Scroll Area Settings").push("scroll_area");
            scrollAreaX = builder
                    .comment("X position of the scroll area")
                    .defineInRange("scrollAreaX", 0, -20, 50);
            scrollAreaY = builder
                    .comment("Y position of the scroll area")
                    .defineInRange("scrollAreaY", 3, -20, 50);
            scrollAreaWidth = builder
                    .comment("Width of the scroll area")
                    .defineInRange("scrollAreaWidth", 164, 50, 500);
            scrollAreaHeight = builder
                    .comment("Height of the scroll area")
                    .defineInRange("scrollAreaHeight", 180, 50, 500);
            builder.pop();

            // 槽位布局
            builder.comment("Slot Layout Settings").push("slots");
            materialAreaStartX = builder
                    .comment("X position of material area")
                    .defineInRange("materialAreaStartX", 1, -50, 100);
            materialAreaStartY = builder
                    .comment("Y position of material area")
                    .defineInRange("materialAreaStartY", 100, 0, 200);
            slotSize = builder
                    .comment("Size of slots in pixels")
                    .defineInRange("slotSize", 18, 8, 32);
            slotsPerRow = builder
                    .comment("Number of slots per row")
                    .defineInRange("slotsPerRow", 9, 1, 18);
            builder.pop();

            // 文本限制
            builder.comment("Text Limits").push("limits");
            maxPartsPerLine = builder
                    .comment("Maximum number of parts per line")
                    .defineInRange("maxPartsPerLine", 3, 1, 10);
            maxTraitLineWidth = builder
                    .comment("Maximum width of trait line before wrapping")
                    .defineInRange("maxTraitLineWidth", 35, 10, 100);
            tooltipWidth = builder
                    .comment("Maximum width of tooltip before wrapping")
                    .defineInRange("tooltipWidth", 35, 10, 100);
            textMaxWidth = builder
                    .comment("Maximum width of text before wrapping")
                    .defineInRange("textMaxWidth", 140, 50, 300);
            builder.pop();

            // 颜色配置
            builder.comment("Color Settings").push("colors");
            textColorGray = builder
                    .comment("Gray text color (hex format)")
                    .define("textColorGray", "0xFF888888");
            textColorPurple = builder
                    .comment("Purple text color (hex format)")
                    .define("textColorPurple", "0xFFAA00AA");
            textColorBlack = builder
                    .comment("Black text color (hex format)")
                    .define("textColorBlack", "0xFF000000");
            textColorDarkGray = builder
                    .comment("Dark gray text color (hex format)")
                    .define("textColorDarkGray", "0xFF555555");
            textColorLabel = builder
                    .comment("Label text color (Minecraft color code)")
                    .define("textColorLabel", "§3");
            textColorValue = builder
                    .comment("Value text color (Minecraft color code)")
                    .define("textColorValue", "§f");
            textColorTitle = builder
                    .comment("Title text color (Minecraft color code)")
                    .define("textColorTitle", "§6§l");
            textColorSubtitle = builder
                    .comment("Subtitle text color (Minecraft color code)")
                    .define("textColorSubtitle", "§b§n");
            textColorPropName = builder
                    .comment("Property name text color (Minecraft color code)")
                    .define("textColorPropName", "§7");
            builder.pop();

            builder.comment("Row Limit Settings").push("row_limits");
            maxMaterialRows = builder
                    .comment("Maximum number of material rows to display (if dynamic calculation is disabled)")
                    .defineInRange("maxMaterialRows", 3, 1, 10);
            fixedMaxRows = builder
                    .comment("Fixed maximum number of rows (used when dynamic calculation is disabled)")
                    .defineInRange("fixedMaxRows", 3, 1, 10);
            enableDynamicRowCalculation = builder
                    .comment("Enable dynamic calculation of rows based on available space (if false, uses fixedMaxRows)")
                    .define("enableDynamicRowCalculation", true);
            builder.pop();

            builder.comment("Search Terms Settings (adds trait/part type names to JEI search index)").push("search_terms");
            enableSearchTerms = builder
                    .comment("""
                Enable adding trait/part type names as search terms for JEI.
                WARNING: Enabling this may cause crashes with mods that use Fabric API (e.g., Forgified Fabric API).
                If you experience 'IncompatibleClassChangeError', disable this and restart.
                Default: false (disabled for stability)""")
                    .define("enableSearchTerms", false);
            requireAdvancedTooltips = builder
                    .comment("Only add search terms when advanced tooltips are shown (F3+H). Default: true")
                    .define("requireAdvancedTooltips", true);
            enableTraitSearch = builder
                    .comment("Enable trait name search terms (e.g., 'sharp', 'durable')")
                    .define("enableTraitSearch", true);
            enablePartTypeSearch = builder
                    .comment("Enable part type search terms (e.g., 'blade', 'handle')")
                    .define("enablePartTypeSearch", true);
            builder.pop();

            builder.pop(); // jei_layout
        }
    }

    public static final ModConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;

    static {
        final Pair<Client, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }
}