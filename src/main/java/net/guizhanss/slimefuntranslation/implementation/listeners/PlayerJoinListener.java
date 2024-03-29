package net.guizhanss.slimefuntranslation.implementation.listeners;

import javax.annotation.Nonnull;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;

public class PlayerJoinListener implements Listener {
    public PlayerJoinListener(@Nonnull SlimefunTranslation plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(@Nonnull PlayerJoinEvent e) {
        SlimefunTranslation.getUserService().addUser(e.getPlayer());
    }
}
