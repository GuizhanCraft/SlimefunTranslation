package net.guizhanss.slimefuntranslation.core.commands.subcommands.translation;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.core.services.localization.Language;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import net.guizhanss.guizhanlib.minecraft.commands.AbstractCommand;
import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.core.commands.AbstractSubCommand;
import net.guizhanss.slimefuntranslation.utils.constant.Permissions;

public class GenerateCommand extends AbstractSubCommand {
    public GenerateCommand(@Nonnull AbstractCommand parent) {
        super(parent, "generate", (cmd, sender) -> getDescription("translation.generate", sender), "<addon> <language> [itemgroup]");
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onExecute(CommandSender sender, String[] args) {
        var translationService = SlimefunTranslation.getTranslationService();
        if (!Permissions.COMMAND_TRANSLATION_GENERATE.hasPermission(sender)) {
            translationService.sendMessage(sender, "no-permission");
        }

        String addonName = args[0];
        String language = args[1];

        if (!SlimefunTranslation.getInstance().getServer().getPluginManager().isPluginEnabled(addonName)) {
            translationService.sendMessage(sender, "sftranslation.commands.translation.generate.invalid-addon", addonName);
            return;
        }

        Set<String> exportIds = new HashSet<>();

        if (args.length == 3) {
            // export every item in the item group
            ItemGroup itemGroup = findItemGroup(addonName.toLowerCase(Locale.ROOT), args[2]);
            if (itemGroup == null) {
                translationService.sendMessage(sender, "sftranslation.commands.translation.generate.invalid-itemgroup", args[2]);
                return;
            }
            exportIds.addAll(
                Slimefun.getRegistry().getAllSlimefunItems().stream()
                    .filter(item -> item.getItemGroup().equals(itemGroup))
                    .map(SlimefunItem::getId)
                    .toList()
            );
        } else {
            // export every item in the addon
            exportIds.addAll(
                Slimefun.getRegistry().getAllSlimefunItems().stream()
                    .filter(item -> item.getItemGroup().getKey().getNamespace().equals(addonName.toLowerCase(Locale.ROOT)))
                    .map(SlimefunItem::getId)
                    .toList()
            );
        }

        final String fileName = SlimefunTranslation.getTranslationService().exportItemTranslations(language, addonName, exportIds);
        translationService.sendMessage(sender, "sftranslation.commands.translation.generate.success", fileName, language);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public List<String> onTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return getAddons(args[0]);
        } else if (args.length == 2) {
            return getLanguages(args[1]);
        } else if (args.length == 3) {
            return getItemGroups(args[2], args[0]);
        } else {
            return List.of();
        }
    }

    @Nonnull
    private List<String> getAddons(@Nonnull String filter) {
        Collection<Plugin> addons = Slimefun.getInstalledAddons();

        List<String> result = addons.stream()
            .filter(addon -> addon.getName().toLowerCase(Locale.ROOT).startsWith(filter.toLowerCase(Locale.ROOT)))
            .map(Plugin::getName)
            .collect(Collectors.toList());
        result.add(0, "Slimefun");
        return result;
    }

    @Nonnull
    private List<String> getLanguages(@Nonnull String filter) {
        Set<String> result = new HashSet<>();

        result.addAll(Slimefun.getLocalization().getLanguages().stream().map(Language::getId).toList());
        result.addAll(SlimefunTranslation.getRegistry().getLanguages());

        return result.stream()
            .filter(lang -> lang.toLowerCase(Locale.ROOT).startsWith(filter.toLowerCase(Locale.ROOT)))
            .toList();
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    private List<String> getItemGroups(String filter, String addon) {
        Collection<ItemGroup> itemGroups = Slimefun.getRegistry().getAllItemGroups();

        return itemGroups.stream()
            .filter(itemGroup -> !(itemGroup instanceof FlexItemGroup))
            .map(itemGroup -> itemGroup.getKey().toString())
            .filter(key -> key.startsWith(addon.toLowerCase(Locale.ROOT) + ":"))
            .filter(key -> key.toLowerCase(Locale.ROOT).startsWith(filter.toLowerCase(Locale.ROOT)))
            .map(key -> key.split(":")[1])
            .toList();
    }

    @Nullable
    @ParametersAreNonnullByDefault
    private ItemGroup findItemGroup(String namespace, String key) {
        for (ItemGroup itemGroup : Slimefun.getRegistry().getAllItemGroups()) {
            NamespacedKey itemGroupKey = itemGroup.getKey();
            if (itemGroupKey.getNamespace().equals(namespace) && itemGroupKey.getKey().equals(key)) {
                return itemGroup;
            }
        }
        return null;
    }
}
