package net.guizhanss.slimefuntranslation.core.services;

import javax.annotation.Nonnull;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.implementation.listeners.PlayerJoinListener;
import net.guizhanss.slimefuntranslation.implementation.listeners.PlayerQuitListener;
import net.guizhanss.slimefuntranslation.implementation.listeners.SlimefunLanguageChangeListener;

public final class ListenerService {
    public ListenerService(@Nonnull SlimefunTranslation plugin) {
        new PlayerJoinListener(plugin);
        new PlayerQuitListener(plugin);
        new SlimefunLanguageChangeListener(plugin);
    }
}
