package com.drowningfish233.silentgearjei.compat.jei;

import com.drowningfish233.silentgearjei.Config;
import com.drowningfish233.silentgearjei.Utils.JEI.PartTypeSearchTerms;
import com.drowningfish233.silentgearjei.Utils.JEI.TraitSearchTerms;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.TooltipFlagExtension;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.Set;

public final class JeiSearchTooltipHandler {
    private JeiSearchTooltipHandler() {
    }

    public static void addMaterialTraitSearchTerms(ItemTooltipEvent event) {
        if (!Config.CLIENT.enableSearchTerms.get()) {
            return;
        }

        TooltipFlagExtension tooltipFlag = (TooltipFlagExtension) event.getFlags();
        if (!tooltipFlag.shouldDisplayAllInformation()) {
            return;
        }

        ItemStack stack = event.getItemStack();

        if (Config.CLIENT.enableTraitSearch.get()) {
            Set<String> traitTerms = TraitSearchTerms.collect(stack);
            for (String searchTerm : traitTerms) {
                event.getToolTip().add(Component.literal(searchTerm));
            }
        }

        if (Config.CLIENT.enablePartTypeSearch.get()) {
            Set<String> partTypeTerms = PartTypeSearchTerms.collect(stack);
            for (String searchTerm : partTypeTerms) {
                event.getToolTip().add(Component.literal(searchTerm));
            }
        }
    }
}