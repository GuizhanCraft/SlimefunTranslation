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
        super(parent, "reload", (cmd, sender) -> getDescription("translation.reload", sender), "");
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onExecute(CommandSender sender, String[] args) {
        var translationService = SlimefunTranslation.getTranslationService();
        if (!Permissions.COMMAND_TRANSLATION_RELOAD.hasPermission(sender)) {
            MESSAGE_FACTORY.sendMessage(sender, "no-permission");
            return;
        }

        translationService.clearTranslations();
        translationService.callLoadEvent();
        MESSAGE_FACTORY.sendMessage(sender, "commands.translation.reload.success");
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public List<String> onTab(CommandSender sender, String[] args) {
        return List.of();
    }
}
