package net.guizhanss.slimefuntranslation.implementation.groups;

import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.core.factories.MessageFactory;
import net.guizhanss.slimefuntranslation.utils.constant.Keys;

/**
 * A fake item group that is used to display search results.
 * <p>
 * Slimefun does not allow other plugins to extend {@code GuideEntry}, so we use an ItemGroup to fill guide history,
 * that the players can go back to our search results, instead of Slimefun's search result.
 */
@SuppressWarnings("deprecation")
public class SearchResultGroup extends FlexItemGroup {
    private static final int[] HEADER = new int[] {0, 2, 3, 4, 5, 6, 8};
    private static final int GUIDE_BACK = 1;
    private static final int GUIDE_SEARCH = 7;
    private static final int RESULT_START = 9;
    private static final int RESULT_END = 44;
    private static final int[] FOOTER = new int[] {45, 46, 47, 48, 49, 50, 51, 52, 53};

    private static final MessageFactory MESSAGE_FACTORY = MessageFactory.get(SlimefunTranslation.getInstance());

    private final String query;

    @ParametersAreNonnullByDefault
    public SearchResultGroup(String query) {
        super(Keys.SEARCH_RESULT_GROUP, new CustomItemStack(
            Material.BARRIER,
            "Fake Search Result (by SlimefunTranslation)"
        ));

        this.query = query;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isVisible(Player p, PlayerProfile profile, SlimefunGuideMode guideMode) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void open(Player p, PlayerProfile profile, SlimefunGuideMode guideMode) {
        final String searchTerm = ChatColor.stripColor(query.toLowerCase(Locale.ROOT));
        ChestMenu menu = new ChestMenu(Slimefun.getLocalization().getMessage(p, "guide.search.inventory").replace("%item%", ChatUtils.crop(ChatColor.WHITE, query)));

        menu.setEmptySlotsClickable(false);
        setupMenu(menu, p, profile, guideMode);
        profile.getGuideHistory().add(this, 1);

        int index = RESULT_START;
        // Find items and add them
        for (SlimefunItem slimefunItem : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            if (index == RESULT_END) {
                break;
            }

            if (!slimefunItem.isHidden() &&
                slimefunItem.getItemGroup().isAccessible(p) &&
                isSearchFilterApplicable(p, slimefunItem, searchTerm)
            ) {
                ItemStack itemstack = new CustomItemStack(slimefunItem.getItem(), meta -> {
                    meta.setDisplayName(SlimefunTranslation.getTranslationService().getTranslatedItemName(
                        SlimefunTranslation.getUserService().getUser(p),
                        slimefunItem
                    ));
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
                        MESSAGE_FACTORY.sendMessage(pl, "sftranslation.commands.search.error");
                        SlimefunTranslation.log(Level.WARNING, x, "Failed to open guide for item" + slimefunItem.getId());
                    }

                    return false;
                });

                index++;
            }
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

    @ParametersAreNonnullByDefault
    private void setupMenu(ChestMenu menu, Player p, PlayerProfile profile, SlimefunGuideMode guideMode) {
        for (int slot : HEADER) {
            menu.addItem(slot, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
        }
        for (int slot : FOOTER) {
            menu.addItem(slot, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
        }

        // sound
        menu.addMenuOpeningHandler(SoundEffect.GUIDE_BUTTON_CLICK_SOUND::playFor);

        // back
        menu.addItem(GUIDE_BACK, ChestMenuUtils.getBackButton(
            p,
            "",
            ChatColor.GRAY + Slimefun.getLocalization().getMessage(p, "guide.back.guide")
        ), (pl, slot, item, action) -> {
            profile.getGuideHistory().goBack(Slimefun.getRegistry().getSlimefunGuide(guideMode));
            return false;
        });

        // search
        menu.addItem(GUIDE_SEARCH, ChestMenuUtils.getSearchButton(p), (pl, slot, item, action) -> {
            pl.closeInventory();
            Slimefun.getLocalization().sendMessage(pl, "guide.search.message");
            // call the search command, things are handled there
            ChatInput.waitForPlayer(Slimefun.instance(), pl, newQuery -> pl.chat("/sftranslation search " + newQuery));
            return false;
        });
    }
}
