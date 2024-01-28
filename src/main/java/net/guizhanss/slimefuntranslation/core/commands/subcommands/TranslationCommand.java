package net.guizhanss.slimefuntranslation.core.commands.subcommands;


import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.command.CommandSender;

import net.guizhanss.guizhanlib.minecraft.commands.AbstractCommand;
import net.guizhanss.guizhanlib.minecraft.commands.SubCommand;
import net.guizhanss.slimefuntranslation.SlimefunTranslation;

public class TranslationCommand extends SubCommand {
    public TranslationCommand(@Nonnull AbstractCommand parent) {
        super(parent, "translation", (cmd, sender) ->
            SlimefunTranslation.getTranslationService().getMessage(sender, "sftranslation.commands.translation.description"), "<subcommands>");
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onExecute(CommandSender sender, String[] args) {
        // TODO: implement translation management
    }
}
