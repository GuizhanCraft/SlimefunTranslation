package net.guizhanss.slimefuntranslation.core.services;

import javax.annotation.Nonnull;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.implementation.listeners.PlayerJoinListener;
import net.guizhanss.slimefuntranslation.implementation.listeners.PlayerQuitListener;
import net.guizhanss.slimefuntranslation.implementation.listeners.SlimefunBlockRightClickListener;
import net.guizhanss.slimefuntranslation.implementation.listeners.SlimefunItemLoadListener;
import net.guizhanss.slimefuntranslation.implementation.listeners.SlimefunLanguageChangeListener;
import net.guizhanss.slimefuntranslation.implementation.listeners.TranslationsLoadListener;

public final class ListenerService {
    public ListenerService(@Nonnull SlimefunTranslation plugin) {
        new PlayerJoinListener(plugin);
        new PlayerQuitListener(plugin);
        new SlimefunBlockRightClickListener(plugin);
        new SlimefunItemLoadListener(plugin);
        new SlimefunLanguageChangeListener(plugin);
        new TranslationsLoadListener(plugin);
    }
}
