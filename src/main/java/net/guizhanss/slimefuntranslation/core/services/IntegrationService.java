package net.guizhanss.slimefuntranslation.core.services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import me.clip.placeholderapi.PlaceholderAPI;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.core.users.User;
import net.guizhanss.slimefuntranslation.integrations.placeholders.SlimefunLoreExpansion;

import lombok.AccessLevel;
import lombok.Getter;

@Getter
public final class IntegrationService {

    @Getter(AccessLevel.NONE)
    private final SlimefunTranslation plugin;

    private final boolean protocolLibEnabled;
    private final boolean placeholderAPIEnabled;

    public IntegrationService(SlimefunTranslation plugin) {
        this.plugin = plugin;

        protocolLibEnabled = isEnabled("ProtocolLib");
        placeholderAPIEnabled = isEnabled("PlaceholderAPI");

        if (protocolLibEnabled) {
            SlimefunTranslation.log(Level.INFO, "ProtocolLib found, enabling packet listener...");
            new PacketListenerService();
        }

        if (placeholderAPIEnabled) {
            SlimefunTranslation.log(Level.INFO, "PlaceholderAPI found, enabling placeholders...");
            new SlimefunLoreExpansion().register();
        }
    }

    private boolean isEnabled(@Nonnull String pluginName) {
        return plugin.getServer().getPluginManager().isPluginEnabled(pluginName);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public String applyPlaceholders(User user, String str) {
        if (!placeholderAPIEnabled) {
            return str;
        }

        return PlaceholderAPI.setPlaceholders(user.getPlayer(), str);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public List<String> applyPlaceholders(User user, List<String> list) {
        if (!placeholderAPIEnabled) {
            return list;
        }

        List<String> result = new ArrayList<>();
        for (String str : list) {
            result.add(applyPlaceholders(user, str));
        }
        return result;
    }
}
