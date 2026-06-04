package com.drowningfish233.silentgearjei.Utils.JEI;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class JeiWidgetFactory {

    public static IRecipeWidget createTextWidget(String text, int x, int y) {
        return createTextWidget(text, x, y, false);
    }

    public static IRecipeWidget createTextWidget(String text, int x, int y, boolean isBold) {
        return new IRecipeWidget() {
            private final ScreenPosition position = new ScreenPosition(x, y);

            @NotNull
            @Override
            public ScreenPosition getPosition() {
                return position;
            }

            @Override
            public void drawWidget(@NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
                if (text == null || text.isEmpty()) return;

                Font font = Minecraft.getInstance().font;
                String displayText = isBold && !text.startsWith("§l") ? "§l" + text : text;

                guiGraphics.drawString(
                        font,
                        displayText,
                        position.x(),
                        position.y(),
                        0xFF000000,
                        false
                );
            }

            @Override
            public void getTooltip(@NotNull ITooltipBuilder tooltip, double mouseX, double mouseY) {}
        };
    }

    public static IRecipeWidget createTextWidgetWithTooltip(JeiLineInfo line, int x) {
        return new IRecipeWidget() {
            private final ScreenPosition position = new ScreenPosition(x, line.y);

            @NotNull
            @Override
            public ScreenPosition getPosition() {
                return position;
            }

            @Override
            public void drawWidget(@NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
                if (line.text == null || line.text.isEmpty()) return;

                Font font = Minecraft.getInstance().font;
                String displayText = line.isBold && !line.text.startsWith("§l") ? "§l" + line.text : line.text;

                guiGraphics.drawString(
                        font,
                        displayText,
                        position.x(),
                        position.y(),
                        0xFF000000,
                        false
                );
            }

            @Override
            public void getTooltip(@NotNull ITooltipBuilder tooltip, double mouseX, double mouseY) {
                if (line.tooltipTitle != null) {
                    tooltip.add(Component.literal("§6" + line.tooltipTitle));
                }

                if (line.tooltipDescription != null && !line.tooltipDescription.isEmpty()) {
                    List<String> descLines = JeiTextUtils.splitDescription(
                            line.tooltipDescription,
                            JeiLayoutConstants.TOOLTIP_WIDTH
                    );
                    for (String descLine : descLines) {
                        tooltip.add(Component.literal("§7" + descLine));
                    }
                } else if (line.tooltipTitle != null) {
                    tooltip.add(Component.translatable(LocalizationKeys.NO_DESCRIPTION));
                }
            }
        };
    }

    private JeiWidgetFactory() {}
}