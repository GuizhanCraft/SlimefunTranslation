package net.guizhanss.slimefuntranslation.implementation.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;

public class PlayerQuitListener implements Listener {
    public PlayerQuitListener(SlimefunTranslation plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerQuitEvent e) {
        SlimefunTranslation.getUserService().removeUser(e.getPlayer());
    }
}
