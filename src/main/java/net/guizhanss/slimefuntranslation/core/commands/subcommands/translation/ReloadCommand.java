package net.guizhanss.slimefuntranslation.core.commands.subcommands.translation;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.command.CommandSender;

import net.guizhanss.guizhanlib.minecraft.commands.AbstractCommand;
import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.core.commands.AbstractSubCommand;
import net.guizhanss.slimefuntranslation.utils.constant.Permissions;

public class ReloadCommand extends AbstractSubCommand {
    public ReloadCommand(@Nonnull AbstractCommand parent) {
        super(parent, "extract", (cmd, sender) -> getDescription("translation.extract", sender), "<replace:true/false>");
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onExecute(CommandSender sender, String[] args) {
        var translationService = SlimefunTranslation.getTranslationService();
        if (!Permissions.COMMAND_TRANSLATION_EXTRACT.hasPermission(sender)) {
            translationService.sendMessage(sender, "no-permission");
        }

        boolean replace = Boolean.valueOf(args[0]);
        SlimefunTranslation.getTranslationService().extractTranslations(replace);
        translationService.sendMessage(sender, "sftranslation.commands.translation.extract.success", replace);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public List<String> onTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return List.of("true", "false");
        } else {
            return List.of();
        }
    }
}
