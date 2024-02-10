package net.guizhanss.slimefuntranslation.api.events;

import javax.annotation.Nonnull;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is fired when SlimefunTranslation starts to load all translations.
 * <p>
 * This is fired after Slimefun finish loading items, or an operator used reload translation command.
 *
 * @author ybw0014
 */
public final class TranslationsLoadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Nonnull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
