package net.guizhanss.slimefuntranslation.implementation.listeners;

import javax.annotation.Nonnull;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.core.users.User;

public class SlimefunBlockRightClickListener implements Listener {
    public SlimefunBlockRightClickListener(@Nonnull SlimefunTranslation plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onRightClick(@Nonnull PlayerRightClickEvent e) {
        User user = SlimefunTranslation.getUserService().getUser(e.getPlayer());
        var sfBlock = e.getSlimefunBlock();
        user.setRecentClickedBlock(sfBlock.orElse(null));
    }
}
