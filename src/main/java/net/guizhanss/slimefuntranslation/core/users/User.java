package net.guizhanss.slimefuntranslation.core.users;

import java.util.UUID;

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

    public User(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        init();
    }

    public User(UUID uuid) {
        this.player = Bukkit.getPlayer(uuid);
        this.uuid = uuid;
        init();
    }

    private void init() {
        var lang = Slimefun.getLocalization().getLanguage(player);
        if (lang != null) {
            locale = lang.getId();
        } else {
            locale = Slimefun.getLocalization().getDefaultLanguage().getId();
        }
    }
}
