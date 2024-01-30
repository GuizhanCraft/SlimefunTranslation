package net.guizhanss.slimefuntranslation.core.commands.subcommands;

import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;

import net.guizhanss.guizhanlib.minecraft.commands.AbstractCommand;
import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.core.commands.AbstractSubCommand;
import net.guizhanss.slimefuntranslation.utils.constant.Keys;
import net.guizhanss.slimefuntranslation.utils.constant.Permissions;

/**
 * The subcommand that searches for Slimefun items with player's language.
 * <p>
 * Guide code from:
 * <a href="https://github.com/Slimefun/Slimefun4/blob/cd3672c3f29dcb9d02d02cd1c80758c3badb6931/src/main/java/io/github/thebusybiscuit/slimefun4/implementation/guide/SurvivalSlimefunGuide.java#L334">Link</a>
 */
public class SearchCommand extends AbstractSubCommand {
    public SearchCommand(@Nonnull AbstractCommand parent) {
        super(parent, "search", (cmd, sender) -> getDescription("search", sender), "<query> [.] [.] [.] [.] [.]");
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onExecute(CommandSender sender, String[] args) {
        var translationService = SlimefunTranslation.getTranslationService();
        if (!(sender instanceof Player p)) {
            translationService.sendMessage(sender, "player-only");
            return;
        }
        if (!Permissions.COMMAND_SEARCH.hasPermission(p)) {
            translationService.sendMessage(sender, "no-permission");
            return;
        }

        final String query = String.join(" ", args);
        PlayerProfile.get(p, profile -> openSearch(profile, query));
    }

    private void openSearch(PlayerProfile profile, String query) {
        Player p = profile.getPlayer();
        if (p == null) {
            return;
        }

        final String searchTerm = ChatColor.stripColor(query.toLowerCase(Locale.ROOT));
        ChestMenu menu = new ChestMenu(Slimefun.getLocalization().getMessage(p, "guide.search.inventory").replace("%item%", ChatUtils.crop(ChatColor.WHITE, query)));

        menu.setEmptySlotsClickable(false);
        profile.getGuideHistory().add(searchTerm);

        int index = 0;
        // Find items and add them
        for (SlimefunItem slimefunItem : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            if (index == 44) {
                break;
            }

            if (!slimefunItem.isHidden() &&
                slimefunItem.getItemGroup().isAccessible(p) &&
                isSearchFilterApplicable(p, slimefunItem, searchTerm)) {
                ItemStack itemstack = new CustomItemStack(slimefunItem.getItem(), meta -> {
                    ItemGroup itemGroup = slimefunItem.getItemGroup();
                    meta.setLore(Arrays.asList("", ChatColor.DARK_GRAY + "\u21E8 " + ChatColor.WHITE + itemGroup.getDisplayName(p)));
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);
                    PersistentDataAPI.setBoolean(meta, Keys.SEARCH_DISPLAY, true);
                });

                menu.addItem(index, itemstack);
                menu.addMenuClickHandler(index, (pl, slot, itm, action) -> {
                    try {
                        SlimefunGuide.displayItem(profile, slimefunItem, true);
                    } catch (Exception | LinkageError x) {
                        SlimefunTranslation.getTranslationService().sendMessage(pl, "sftranslation.commands.search.error");
                        SlimefunTranslation.log(Level.WARNING, x, "Failed to open guide for item" + slimefunItem.getId());
                    }

                    return false;
                });

                index++;
            }
        }

        for (int i = 45; i < 54; i++) {
            menu.addItem(i, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
        }

        menu.open(p);
    }

    @ParametersAreNonnullByDefault
    private boolean isSearchFilterApplicable(Player p, SlimefunItem slimefunItem, String query) {
        String originalItemName = SlimefunTranslation.getTranslationService().getTranslatedItemName(
            SlimefunTranslation.getUserService().getUser(p),
            slimefunItem
        );
        String itemName = ChatColor.stripColor(originalItemName).toLowerCase(Locale.ROOT);
        return !itemName.isEmpty() && itemName.contains(query);
    }
}
