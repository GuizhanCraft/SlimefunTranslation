package net.guizhanss.slimefuntranslation.core.commands.subcommands;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.command.CommandSender;

import net.guizhanss.guizhanlib.minecraft.commands.AbstractCommand;
import net.guizhanss.guizhanlib.minecraft.commands.SubCommand;
import net.guizhanss.slimefuntranslation.SlimefunTranslation;

public class SearchCommand extends SubCommand {
    public SearchCommand(@Nonnull AbstractCommand parent) {
        super(parent, "search", (cmd, sender) ->
            SlimefunTranslation.getTranslationService().getMessage(sender, "sftranslation.commands.search.description"), "<query>");
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onExecute(CommandSender sender, String[] args) {
    }
}
