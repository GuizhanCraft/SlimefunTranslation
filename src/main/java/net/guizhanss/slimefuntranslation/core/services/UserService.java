package net.guizhanss.slimefuntranslation.core.services;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.core.users.User;

public final class UserService {

    @Nonnull
    public User getUser(@Nonnull UUID uuid) {
        var users = SlimefunTranslation.getRegistry().getUsers();
        if (users.containsKey(uuid)) {
            return users.get(uuid);
        } else {
            var user = new User(uuid);
            users.put(uuid, user);
            return user;
        }
    }

    @Nonnull
    public User getUser(@Nonnull Player p) {
        return getUser(p.getUniqueId());
    }

    public void addUser(@Nonnull UUID uuid) {
        SlimefunTranslation.getRegistry().getUsers().putIfAbsent(uuid, new User(uuid));
    }

    public void addUser(@Nonnull Player p) {
        addUser(p.getUniqueId());
    }

    public void removeUser(@Nonnull UUID uuid) {
        SlimefunTranslation.getRegistry().getUsers().remove(uuid);
    }

    public void removeUser(@Nonnull Player p) {
        removeUser(p.getUniqueId());
    }
}
