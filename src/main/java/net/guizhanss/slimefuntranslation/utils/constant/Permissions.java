package net.guizhanss.slimefuntranslation.utils.constant;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Permissions {
    COMMAND_ID("sftranslation.command.id"),
    COMMAND_SEARCH("sftranslation.command.search");

    private final String permission;

    public boolean hasPermission(@Nonnull Player p) {
        Preconditions.checkArgument(p != null, "Player cannot be null!");
        return p.hasPermission(permission);
    }
}
