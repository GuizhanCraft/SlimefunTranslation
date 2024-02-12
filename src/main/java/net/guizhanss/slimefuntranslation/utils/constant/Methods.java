package net.guizhanss.slimefuntranslation.utils.constant;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.UtilityClass;

/**
 * Method related constants.
 *
 * @author ybw0014
 */
@UtilityClass
public final class Methods {
    /**
     * Method name of a SlimefunItem that provides translated item name.
     */
    public static final String TRANSLATABLE_ITEM_GET_NAME = "getTranslatedItemName";
    /**
     * Method parameters of a SlimefunItem that provides translated item name.
     * <p>
     * player, lang, item, originalName
     */
    public static final Class<?>[] TRANSLATABLE_ITEM_GET_NAME_PARAMS = new Class[] {Player.class, String.class, ItemStack.class, String.class};
    /**
     * Method name of a SlimefunItem that provides translated item lore.
     */
    public static final String TRANSLATABLE_ITEM_GET_LORE = "getTranslatedItemLore";
    /**
     * Method parameters of a SlimefunItem that provides translated item lore.
     * <p>
     * player, lang, item, originalLore
     */
    public static final Class<?>[] TRANSLATABLE_ITEM_GET_LORE_PARAMS = new Class[] {Player.class, String.class, ItemStack.class, List.class};
}
