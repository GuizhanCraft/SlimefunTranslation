package net.guizhanss.slimefuntranslation.core.factories;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Preconditions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.guizhanss.guizhanlib.minecraft.utils.ChatUtil;
import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.core.users.User;
import net.guizhanss.slimefuntranslation.utils.TranslationUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * A factory to get the translated messages for a plugin.
 */
public class MessageFactory {
    private static final Map<String, MessageFactory> INSTANCES = new HashMap<>();

    private final Plugin plugin;

    private MessageFactory(Plugin plugin) {
        this.plugin = plugin;
    }

    @Nonnull
    public static MessageFactory get(@Nonnull Plugin plugin) {
        Preconditions.checkArgument(plugin != null, "plugin cannot be null");
        return INSTANCES.computeIfAbsent(plugin.getName(), k -> new MessageFactory(plugin));
    }

    /**
     * Send a translated message to the given {@link CommandSender}.
     * When the sender is a {@link Player}, the message will be translated based on the player's language.
     * Otherwise, the message will be translated based on the default language.
     *
     * @param sender The {@link CommandSender}.
     * @param key    The key of the message.
     * @param args   The arguments to be applied to the message.
     */
    @ParametersAreNonnullByDefault
    public void sendMessage(CommandSender sender, String key, Object... args) {
        Preconditions.checkArgument(sender != null, "sender cannot be null");
        Preconditions.checkArgument(key != null, "key cannot be null");
        sender.sendMessage(getMessage(sender, key, args));
    }

    /**
     * Send a translated message via the action bar to the given {@link User}.
     *
     * @param user The {@link User}.
     * @param key  The key of the message.
     * @param args The arguments to be applied to the message.
     */
    @ParametersAreNonnullByDefault
    public void sendActionbarMessage(User user, String key, Object... args) {
        Preconditions.checkArgument(user != null, "user cannot be null");
        Preconditions.checkArgument(key != null, "key cannot be null");
        user.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(getMessage(user, key, args)));
    }

    @ParametersAreNonnullByDefault
    public String getMessage(CommandSender sender, String key, Object... args) {
        Preconditions.checkArgument(sender != null, "sender cannot be null");
        User user = null;
        if (sender instanceof Player p) {
            user = SlimefunTranslation.getUserService().getUser(p);
        }
        return getMessage(user, key, args);
    }

    /**
     * Get the translated message for the given {@link User}.
     *
     * @param user The {@link User}.
     * @param key  The key of the message.
     * @param args The arguments to be applied to the message.
     * @return The translated message. Will return the key if the translation does not exist.
     */
    @Nonnull
    public String getMessage(@Nullable User user, @Nonnull String key, @Nonnull Object... args) {
        Preconditions.checkArgument(key != null, "key cannot be null");
        var messageTranslations = SlimefunTranslation.getRegistry().getMessageTranslations();
        if (!messageTranslations.containsKey(plugin.getName())) {
            return key;
        }
        var transl = TranslationUtils.findTranslation(
            messageTranslations.get(plugin.getName()), user, key);
        if (transl.isEmpty()) {
            return key;
        }

        String message = MessageFormat.format(transl.get(), args);
        if (user != null) {
            message = SlimefunTranslation.getIntegrationService().applyPlaceholders(user, message);
        }
        return ChatUtil.color(message);
    }
}
