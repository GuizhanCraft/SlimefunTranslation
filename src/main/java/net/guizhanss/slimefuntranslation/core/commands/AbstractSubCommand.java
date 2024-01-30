package net.guizhanss.slimefuntranslation.core.commands;

import java.util.function.BiFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.command.CommandSender;

import net.guizhanss.guizhanlib.minecraft.commands.AbstractCommand;
import net.guizhanss.guizhanlib.minecraft.commands.SubCommand;
import net.guizhanss.slimefuntranslation.SlimefunTranslation;

public abstract class AbstractSubCommand extends SubCommand {
    protected AbstractSubCommand(
        @Nullable AbstractCommand parent,
        @Nonnull String name,
        @Nonnull BiFunction<AbstractCommand, CommandSender, String> description,
        @Nonnull String usage,
        @Nonnull SubCommand... subCommands
    ) {
        super(parent, name, description, usage, subCommands);
    }

    @ParametersAreNonnullByDefault
    protected AbstractSubCommand(
        String name,
        BiFunction<AbstractCommand, CommandSender, String> description,
        String usage,
        SubCommand... subCommands
    ) {
        super(name, description, usage, subCommands);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    protected static String getDescription(String key, CommandSender sender) {
        return SlimefunTranslation.getTranslationService().getMessage(sender, "sftranslation.commands." + key + ".description");
    }
}
