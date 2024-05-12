package net.guizhanss.slimefuntranslation.implementation.listeners;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;

public class SearchListener implements Listener {
    public SearchListener(@Nonnull SlimefunTranslation plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSearch(@Nonnull InventoryClickEvent e) {
        var item = e.getCurrentItem();
        if (item == null || item.getType().isAir()) {
            return;
        }

        var sfId = Slimefun.getItemDataService().getItemData(item);
        SlimefunTranslation.debug("player clicked on item with sfId: " + sfId.orElse("null"));
        if (sfId.isEmpty() || !sfId.get().equals("_UI_SEARCH")) {
            return;
        }

        var p = (Player) e.getWhoClicked();

        e.setCancelled(true);
        p.closeInventory();

        SlimefunTranslation.debug("intercepting search");
        Slimefun.getLocalization().sendMessage(p, "guide.search.message");
        // call the search command, things are handled there
        ChatInput.waitForPlayer(Slimefun.instance(), p, query -> p.chat("/sftranslation search " + query));
    }
}
