package net.guizhanss.slimefuntranslation.implementation.listeners;

import javax.annotation.Nonnull;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerLanguageChangeEvent;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.core.users.User;

public class SlimefunLanguageChangeListener implements Listener {
    public SlimefunLanguageChangeListener(@Nonnull SlimefunTranslation plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onLanguageChange(@Nonnull PlayerLanguageChangeEvent e) {
        User user = SlimefunTranslation.getUserService().getUser(e.getPlayer());
        user.setLocale(e.getNewLanguage().getId());
    }
}
