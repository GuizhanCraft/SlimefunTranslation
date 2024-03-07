package net.guizhanss.slimefuntranslation.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class GeneralUtils {
    public static boolean isAllNull(Object... objects) {
        for (var object : objects) {
            if (object != null) {
                return false;
            }
        }
        return true;
    }
}
