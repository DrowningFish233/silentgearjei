package com.drowningfish233.silentgearjei.Utils.JEI;

public class JeiLineInfo {
    public final int y;
    public final String text;
    public final boolean isBold;
    public final String tooltipTitle;
    public final String tooltipDescription;
    public final String translationKey;
    public final Object[] translationArgs;

    // 简单文本行
    public JeiLineInfo(int y, String text) {
        this(y, text, false, null, null, null, null);
    }

    // 带格式的文本行
    public JeiLineInfo(int y, String text, boolean isBold) {
        this(y, text, isBold, null, null, null, null);
    }

    // 带tooltip的文本行
    public JeiLineInfo(int y, String text, String tooltipTitle, String tooltipDescription) {
        this(y, text, false, tooltipTitle, tooltipDescription, null, null);
    }

    // 带tooltip和格式的文本行
    public JeiLineInfo(int y, String text, boolean isBold, String tooltipTitle, String tooltipDescription) {
        this(y, text, isBold, tooltipTitle, tooltipDescription, null, null);
    }

    public JeiLineInfo(int y, String text, boolean isBold, String tooltipTitle, String tooltipDescription, String translationKey, Object[] translationArgs) {
        this.y = y;
        this.text = text;
        this.isBold = isBold;
        this.tooltipTitle = tooltipTitle;
        this.tooltipDescription = tooltipDescription;
        this.translationKey = translationKey;
        this.translationArgs = translationArgs;
    }

    public boolean hasTooltip() {
        return tooltipTitle != null || tooltipDescription != null;
    }

    public boolean isTranslatable() {
        return translationKey != null;
    }
}