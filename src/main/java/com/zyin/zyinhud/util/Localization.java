package com.zyin.zyinhud.util;

import net.minecraft.client.resources.I18n;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class Localization
{
    /**
     * Gets the local text of your translation based on the given key.
     * This will look through your mod's translation file that was previously registered.
     * @param key E.x.: tile.block.name
     * @return The translated string or the default English (en_US) translation if none was found.
     */
    public static String get(String key)
    {
    	return I18n.format(key, new Object[0]);
    }
}