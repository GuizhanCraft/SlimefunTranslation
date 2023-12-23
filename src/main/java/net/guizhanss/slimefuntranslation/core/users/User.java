package net.guizhanss.slimefuntranslation.core.users;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import lombok.Getter;
import lombok.ToString;

/**
 * The {@link User} class holds information of a {@link Player}.
 */
@Getter
@ToString
public class User {
    private final Player player;
    private final UUID uuid;
    private String locale;

    public User(@Nonnull Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        init();
    }

    public User(@Nonnull UUID uuid) {
        this.player = Bukkit.getPlayer(uuid);
        this.uuid = uuid;
        init();
    }

    private void init() {
        updateLocale();
    }

    public void updateLocale() {
        var lang = Slimefun.getLocalization().getLanguage(player);
        if (lang != null) {
            locale = lang.getId();
        } else {
            locale = Slimefun.getLocalization().getDefaultLanguage().getId();
        }
    }

    public void updateLocale(@Nonnull String newLocale) {
        Preconditions.checkArgument(newLocale != null, "Locale cannot be null");
        locale = newLocale;
    }
}
