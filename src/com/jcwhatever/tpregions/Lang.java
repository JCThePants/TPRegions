package com.jcwhatever.tpregions;

import com.jcwhatever.nucleus.managed.language.Localized;
import com.jcwhatever.nucleus.utils.text.components.IChatMessage;

/**
 * Utility to reduce the amount of code needed to use the language manager.
 */
public class Lang {

    private Lang() {}

    @Localized
    public static IChatMessage get(CharSequence text, Object... params) {
        return TPRegions.getPlugin().getLanguageContext().get(text, params);
    }
}