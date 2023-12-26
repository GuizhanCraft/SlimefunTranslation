package net.guizhanss.slimefuntranslation.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.guizhanss.slimefuntranslation.api.interfaces.Translation;
import net.guizhanss.slimefuntranslation.core.users.User;

import lombok.Getter;

@Getter
public final class Registry {
    private final Map<UUID, User> users = new HashMap<>();
    private final Map<String, Map<String, Translation>> itemTranslations = new HashMap<>();
    private final Map<String, Map<String, String>> loreTranslations = new HashMap<>();
}
