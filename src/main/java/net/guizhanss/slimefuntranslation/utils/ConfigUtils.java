package net.guizhanss.slimefuntranslation.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import io.github.thebusybiscuit.slimefun4.libraries.dough.common.CommonPatterns;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.utils.constant.Patterns;
import net.guizhanss.slimefuntranslation.utils.tags.SlimefunTranslationTag;

import lombok.experimental.UtilityClass;

@SuppressWarnings("unchecked")
@UtilityClass
public final class ConfigUtils {

    @Nonnull
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

    @Nonnull
    public static Set<Material> parseMaterials(@Nonnull List<String> materialList) {
        Preconditions.checkArgument(materialList != null, "materialList cannot be null");

        Set<Material> materials = new HashSet<>();
        for (String value : materialList) {
            try {
                if (Patterns.MINECRAFT_NAMESPACEDKEY.matcher(value).matches()) {
                    Material material = Material.matchMaterial(value);

                    if (material != null) {
                        materials.add(material);
                    } else {
                        throw new InvalidConfigurationException("Invalid minecraft material: " + value);
                    }
                } else if (Patterns.SFT_TAG_CONFIG.matcher(value).matches()) {
                    String keyValue = CommonPatterns.COLON.split(value)[1].toUpperCase(Locale.ROOT);
                    SlimefunTranslationTag tag = SlimefunTranslationTag.getTag(keyValue);

                    if (tag != null) {
                        materials.addAll(tag.getValues());
                    } else {
                        throw new InvalidConfigurationException("Invalid SlimefunTranslationTag: " + keyValue);
                    }
                }
            } catch (InvalidConfigurationException ex) {
                SlimefunTranslation.log(Level.SEVERE, ex.getMessage(), ex);
            }

        }
        return materials;
    }
}
