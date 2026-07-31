package com.drowningfish233.silentgearjei.compat.jei;

import com.drowningfish233.silentgearjei.Utils.JEI.TraitSearchTerms;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.extensions.TooltipFlagExtension;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.Set;

public final class JeiSearchTooltipHandler {
    private JeiSearchTooltipHandler() {
    }

    public static void addMaterialTraitSearchTerms(ItemTooltipEvent event) {
		TooltipFlagExtension tooltipFlag = event.getFlags();
	    if (!tooltipFlag.shouldDisplayAllInformation()) {
            return;
        }

        Set<String> searchTerms = TraitSearchTerms.collect(event.getItemStack());
        if (searchTerms.isEmpty()) {
            return;
        }

        for (String searchTerm : searchTerms) {
            event.getToolTip().add(Component.literal(searchTerm));
        }
    }
}
