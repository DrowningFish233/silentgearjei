package com.drowningfish233.silentgearjei.Utils.JEI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

public final class JeiTextUtils {

    // 罗马数字转换
    public static String toRomanNumeral(int number) {
        if (number < 1 || number > 3999) return String.valueOf(number);

        String[] romanSymbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            while (number >= values[i]) {
                number -= values[i];
                result.append(romanSymbols[i]);
            }
        }
        return result.toString();
    }

    // 分割文本（按单词）
    public static List<String> splitText(String text, int maxLength) {
        List<String> lines = new ArrayList<>();
        String remaining = text;

        while (remaining.length() > maxLength) {
            int splitPos = remaining.lastIndexOf(' ', maxLength);
            if (splitPos == -1) splitPos = maxLength;

            lines.add(remaining.substring(0, splitPos));
            remaining = remaining.substring(splitPos).trim();
        }

        if (!remaining.isEmpty()) {
            lines.add(remaining);
        }

        return lines;
    }

    // 分割描述文本
    public static List<String> splitDescription(String text, int maxLength) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (currentLine.length() + word.length() + 1 <= maxLength) {
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                }
                currentLine = new StringBuilder(word);
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    // 获取格式化文本宽度
    public static int getStringWidth(String text) {
        Font font = Minecraft.getInstance().font;
        String plainText = SilentGearJeiUtils.FORMAT_CODES.matcher(text).replaceAll("");
        return font.width(plainText);
    }

    // 创建带格式的组件
    public static MutableComponent formatted(String key, Object... args) {
        return Component.translatable(key, args);
    }

    // 创建带颜色的组件
    public static MutableComponent colored(String key, String colorCode, Object... args) {
        return Component.literal(colorCode).append(Component.translatable(key, args));
    }

    private JeiTextUtils() {}
}