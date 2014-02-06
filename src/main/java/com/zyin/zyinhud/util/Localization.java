package com.zyin.zyinhud.util;

import cpw.mods.fml.common.registry.LanguageRegistry;

public class Localization
{
    /**
     * Gets the local text of your translation based on the given key. This will look through your mod's translation file that was previously registered. Make
     * sure you enter the full name
     * 
     * @param key
     *            - e.g tile.block.name
     * @return The translated string or the default English (en_US) translation if none was found.
     */
    public static String get(String key) {
        String text = LanguageRegistry.instance().getStringLocalization(key);

        if (text == null || text == "") {
            text = LanguageRegistry.instance().getStringLocalization(key, "en_US");
        }

        return text;
    }
}