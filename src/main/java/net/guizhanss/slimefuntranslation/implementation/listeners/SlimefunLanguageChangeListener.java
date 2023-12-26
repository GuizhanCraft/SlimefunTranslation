package net.guizhanss.slimefuntranslation.implementation.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerLanguageChangeEvent;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.core.users.User;

public class SlimefunLanguageChangeListener implements Listener {
    public SlimefunLanguageChangeListener(SlimefunTranslation plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerLanguageChangeEvent e) {
        User user = SlimefunTranslation.getUserService().getUser(e.getPlayer());
        user.updateLocale(e.getNewLanguage().getId());
    }
}
