package com.drowningfish233.silentgearjei;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;

@Mod(SilentGearJei.MODID)
public class SilentGearJei {
    public static final String MODID = "silentgearjei";

    public SilentGearJei(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
    }
}