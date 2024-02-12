package net.guizhanss.slimefuntranslation.utils;

import javax.annotation.ParametersAreNonnullByDefault;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ReflectionUtils {
    @ParametersAreNonnullByDefault
    public static boolean hasMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            clazz.getDeclaredMethod(methodName, parameterTypes);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
