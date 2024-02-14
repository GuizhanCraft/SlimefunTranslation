package net.guizhanss.slimefuntranslation.utils.constant;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import org.bukkit.command.CommandSender;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Permissions {
    COMMAND_ID("sftranslation.command.id"),
    COMMAND_SEARCH("sftranslation.command.search"),
    COMMAND_TRANSLATION_EXTRACT("sftranslation.command.translation.extract"),
    COMMAND_TRANSLATION_GENERATE("sftranslation.command.translation.generate"),
    COMMAND_TRANSLATION_RELOAD("sftranslation.command.translation.reload");

    private final String permission;

    public boolean hasPermission(@Nonnull CommandSender sender) {
        Preconditions.checkArgument(sender != null, "sender cannot be null");
        return sender.hasPermission(permission);
    }
}
