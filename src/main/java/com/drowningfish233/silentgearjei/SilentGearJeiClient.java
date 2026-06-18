package com.drowningfish233.silentgearjei;

import com.drowningfish233.silentgearjei.Utils.JEI.JeiLayoutConstants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(value = SilentGearJei.MODID, dist = Dist.CLIENT)   
public class SilentGearJeiClient {

    public SilentGearJeiClient(ModContainer container, IEventBus modEventBus) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(this::onClientSetup);
        }

        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        JeiLayoutConstants.init();
    }
}