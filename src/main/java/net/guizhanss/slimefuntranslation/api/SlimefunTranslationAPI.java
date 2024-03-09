package net.guizhanss.slimefuntranslation.api;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Preconditions;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.core.factories.MessageFactory;
import net.guizhanss.slimefuntranslation.core.lore.LoreHandler;
import net.guizhanss.slimefuntranslation.core.users.User;

import lombok.experimental.UtilityClass;

/**
 * You should use this class to interact with SlimefunTranslation.
 * <p>
 * The methods in service classes are subject to change.
 */
@UtilityClass
public final class SlimefunTranslationAPI {
    /**
     * Get the SlimefunTranslation {@link User} by their {@link UUID}.
     *
     * @param uuid The {@link UUID} of the user.
     * @return The {@link User} with the given {@link UUID}.
     */
    @Nonnull
    public static User getUser(@Nonnull UUID uuid) {
        Preconditions.checkArgument(uuid != null, "UUID cannot be null");

        return SlimefunTranslation.getUserService().getUser(uuid);
    }

    /**
     * Get the SlimefunTranslation {@link User} by {@link Player} object.
     *
     * @param player The {@link Player} object of the user.
     * @return The {@link User} with the given {@link Player}.
     */
    @Nonnull
    public static User getUser(@Nonnull Player player) {
        Preconditions.checkArgument(player != null, "Player cannot be null");

        return getUser(player.getUniqueId());
    }

    /**
     * Get the translated name of a {@link SlimefunItem} for the {@link User}.
     *
     * @param user   The {@link User} to translate the item name for.
     * @param sfItem The {@link SlimefunItem} to translate.
     * @return The translated name of the item.
     * Will return an empty string when the item is null, or the original item name if the translation is disabled.
     */
    @Nonnull
    public static String getItemName(@Nonnull User user, @Nullable SlimefunItem sfItem) {
        Preconditions.checkArgument(user != null, "User cannot be null");

        return SlimefunTranslation.getTranslationService().getTranslatedItemName(user, sfItem);
    }

    /**
     * Get the translated name of a Slimefun item with given ID for the {@link User}.
     *
     * @param user The {@link User} to translate the item name for.
     * @param id   The ID of the {@link SlimefunItem} to translate.
     * @return The translated name of the item.
     * Will return an empty string when the item is null, or the original item name if the translation is disabled.
     */
    @Nonnull
    @ParametersAreNonnullByDefault
    public static String getItemName(User user, String id) {
        Preconditions.checkArgument(user != null, "User cannot be null");
        Preconditions.checkArgument(id != null, "ID cannot be null");

        return SlimefunTranslation.getTranslationService().getTranslatedItemName(user, SlimefunItem.getById(id));
    }

    /**
     * Translates the given {@link ItemStack} for the {@link User}.
     *
     * @param user The {@link User} to translate the item for.
     * @param item The {@link ItemStack} to translate.
     * @return Whether the translation is processed.
     */
    @ParametersAreNonnullByDefault
    public static boolean translateItem(User user, ItemStack item) {
        Preconditions.checkArgument(user != null, "User cannot be null");
        Preconditions.checkArgument(item != null, "Item cannot be null");

        return SlimefunTranslation.getTranslationService().translateItem(user, item);
    }

    /**
     * Get the specified lore line for the {@link User}.
     *
     * @param user        The {@link User} to translate the lore for.
     * @param id          The id of the lore.
     * @param defaultToId Whether to return the ID if the translation is not found. If false, will return an empty string.
     * @return The translated lore line.
     */
    @Nonnull
    @ParametersAreNonnullByDefault
    public static String getLore(User user, String id, boolean defaultToId) {
        Preconditions.checkArgument(user != null, "User cannot be null");
        Preconditions.checkArgument(id != null, "ID cannot be null");

        return SlimefunTranslation.getTranslationService().getLore(user, id, defaultToId);
    }

    @ParametersAreNonnullByDefault
    public static void registerLoreHandler(String id, LoreHandler handler) {
        Preconditions.checkArgument(id != null, "ID cannot be null");
        Preconditions.checkArgument(handler != null, "Handler cannot be null");

        SlimefunTranslation.getRegistry().getSlimefunLoreHandlers().put(id, handler);
    }

    /**
     * Get the {@link MessageFactory} for the given {@link Plugin}.
     *
     * @param plugin The {@link Plugin} to get the {@link MessageFactory} for.
     * @return The {@link MessageFactory} for the given {@link Plugin}.
     */
    @Nonnull
    public static MessageFactory getMessageFactory(@Nonnull Plugin plugin) {
        Preconditions.checkArgument(plugin != null, "Plugin cannot be null");

        return MessageFactory.get(plugin);
    }
}
