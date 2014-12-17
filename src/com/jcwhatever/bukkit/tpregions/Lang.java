package com.jcwhatever.bukkit.tpregions;

import com.jcwhatever.bukkit.generic.language.Localized;

/**
 * Utility to reduce the amount of code needed to use the language manager.
 */
public class Lang {

    private Lang() {}

    @Localized
    public static String get(String text, Object... params) {
        return TPRegions.getPlugin().getLanguageManager().get(text, params);
    }
}