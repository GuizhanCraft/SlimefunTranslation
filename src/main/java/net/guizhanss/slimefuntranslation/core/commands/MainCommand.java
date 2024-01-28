package net.guizhanss.slimefuntranslation.core.commands;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import net.guizhanss.guizhanlib.minecraft.commands.BaseCommand;
import net.guizhanss.slimefuntranslation.core.commands.subcommands.IdCommand;
import net.guizhanss.slimefuntranslation.core.commands.subcommands.SearchCommand;
import net.guizhanss.slimefuntranslation.core.commands.subcommands.TranslationCommand;

public class MainCommand extends BaseCommand {
    public MainCommand(@Nonnull PluginCommand command) {
        super(command, (cmd, sender) -> "", "<subcommand>");
        addSubCommand(new IdCommand(this));
        addSubCommand(new SearchCommand(this));
        addSubCommand(new TranslationCommand(this));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onExecute(CommandSender sender, String[] args) {
        // we have subcommands so this method doesn't need to do anything
    }
}
