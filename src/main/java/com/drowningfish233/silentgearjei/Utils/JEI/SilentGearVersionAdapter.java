package com.drowningfish233.silentgearjei.Utils.JEI;

import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.api.property.GearProperty;
import net.silentchaos512.gear.api.property.GearPropertyMap;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import java.util.Collection;

public class SilentGearVersionAdapter {

    public static Component formatTextUnchecked(
            Collection<? extends GearPropertyValue<?>> mods,
            GearProperty<?, ?> property) {

        try {
            return callNewVersionMethod(mods, property);
        } catch (Exception e) {
            return callOldVersionMethod(mods, property);
        }
    }

    private static Component callNewVersionMethod(
            Collection<? extends GearPropertyValue<?>> mods,
            GearProperty<?, ?> property) {
        try {
            Class<?> colorSchemeClass = Class.forName("net.silentchaos512.gear.client.tooltip.FormatColorScheme");

            Object defaultScheme = colorSchemeClass.getField("DEFAULT").get(null);

            java.lang.reflect.Method method = GearPropertyMap.class.getMethod(
                    "formatTextUnchecked",
                    Collection.class,
                    GearProperty.class,
                    colorSchemeClass
            );

            return (Component) method.invoke(null, mods, property, defaultScheme);
        } catch (Exception e) {
            throw new RuntimeException("Failed to call new version method", e);
        }
    }

    private static Component callOldVersionMethod(
            Collection<? extends GearPropertyValue<?>> mods,
            GearProperty<?, ?> property) {
        try {
            java.lang.reflect.Method method = GearPropertyMap.class.getMethod(
                    "formatTextUnchecked",
                    Collection.class,
                    GearProperty.class,
                    boolean.class
            );

            return (Component) method.invoke(null, mods, property, false);
        } catch (Exception e) {
            return fallbackFormat(mods);
        }
    }

    private static Component fallbackFormat(Collection<? extends GearPropertyValue<?>> mods) {
        net.minecraft.network.chat.MutableComponent result = Component.literal("");
        boolean first = true;

        for (GearPropertyValue<?> mod : mods) {
            if (!first) {
                result.append(", ");
            }
            result.append(String.valueOf(mod.value()));
            first = false;
        }

        return result;
    }
}