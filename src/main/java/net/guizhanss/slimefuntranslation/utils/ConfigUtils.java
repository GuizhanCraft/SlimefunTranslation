package net.guizhanss.slimefuntranslation.utils;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.bukkit.configuration.ConfigurationSection;

import lombok.experimental.UtilityClass;

@SuppressWarnings("unchecked")
@UtilityClass
public final class ConfigUtils {

    public static <V> Map<String, V> getMap(@Nullable ConfigurationSection section) {
        Map<String, V> map = new HashMap<>();
        if (section == null) {
            return map;
        }
        for (String key : section.getKeys(false)) {
            var value = section.get(key);
            if (value instanceof String || value instanceof Integer || value instanceof Double || value instanceof Boolean) {
                map.put(key, (V) value);
            }
        }
        return map;
    }
}
