package net.guizhanss.slimefuntranslation.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.guizhanss.slimefuntranslation.api.translation.ItemTranslation;
import net.guizhanss.slimefuntranslation.core.users.User;

import lombok.Getter;

@Getter
public final class Registry {
    private final Map<UUID, User> users = new HashMap<>();
    private final Set<String> languages = new HashSet<>();
    // lang -> itemId -> translation
    private final Map<String, Map<String, ItemTranslation>> itemTranslations = new HashMap<>();
    // lang -> loreId -> translation
    private final Map<String, Map<String, String>> loreTranslations = new HashMap<>();
    // plugin -> lang -> messageId -> translation
    private final Map<String, Map<String, Map<String, String>>> messageTranslations = new HashMap<>();
}
