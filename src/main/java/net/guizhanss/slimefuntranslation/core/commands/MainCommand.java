package net.guizhanss.slimefuntranslation.core.commands;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import net.guizhanss.guizhanlib.minecraft.commands.BaseCommand;
import net.guizhanss.slimefuntranslation.core.commands.subcommands.IdCommand;
import net.guizhanss.slimefuntranslation.core.commands.subcommands.SearchCommand;
import net.guizhanss.slimefuntranslation.core.commands.subcommands.TranslationCommand;

import java.util.Arrays;
import java.util.List;

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

    @Override
    @Nullable
    @ParametersAreNonnullByDefault
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        System.out.println(args.length);
        System.out.println(Arrays.toString(args));
        return onTabCompleteExecute(sender, args);
    }
}
