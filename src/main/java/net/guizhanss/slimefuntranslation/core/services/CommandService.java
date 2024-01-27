package net.guizhanss.slimefuntranslation.core.services;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.core.commands.MainCommand;

public final class CommandService {
    public CommandService(SlimefunTranslation plugin) {
        new MainCommand(plugin.getCommand("sftranslation")).register();
    }
}
