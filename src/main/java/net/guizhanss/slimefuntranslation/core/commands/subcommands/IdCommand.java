package net.guizhanss.slimefuntranslation.core.commands.subcommands;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.guizhanss.guizhanlib.minecraft.commands.AbstractCommand;
import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.core.commands.AbstractSubCommand;
import net.guizhanss.slimefuntranslation.utils.SlimefunItemUtils;
import net.guizhanss.slimefuntranslation.utils.constant.Permissions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

/**
 * The subcommand that displays the ID of the Slimefun item in the player's hand.
 * <p>
 * Code from StarWishsama's Slimefun4 fork:
 * <a href="https://github.com/StarWishsama/Slimefun4/blob/master/src/main/java/io/github/thebusybiscuit/slimefun4/core/commands/subcommands/ItemIdCommand.java">Link</a>
 */
public class IdCommand extends AbstractSubCommand {
    public IdCommand(@Nonnull AbstractCommand parent) {
        super(parent, "id", (cmd, sender) -> getDescription("id", sender), "");
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onExecute(CommandSender sender, String[] args) {
        var translationService = SlimefunTranslation.getTranslationService();
        if (!(sender instanceof Player p)) {
            translationService.sendMessage(sender, "player-only");
            return;
        }
        if (!Permissions.COMMAND_ID.hasPermission(p)) {
            translationService.sendMessage(sender, "no-permission");
            return;
        }

        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            translationService.sendMessage(sender, "sftranslation.commands.id.not-sf-item");
            return;
        }

        String sfId = SlimefunItemUtils.getId(item);
        if (sfId == null) {
            translationService.sendMessage(sender, "sftranslation.commands.id.not-sf-item");
            return;
        }

        TextComponent msg = new TextComponent(translationService.getMessage(sender, "sftranslation.commands.id.result"));
        msg.setColor(ChatColor.YELLOW);
        String clickToCopy = translationService.getMessage(sender, "sftranslation.commands.id.click-to-copy");
        var idMsg = new TextComponent(sfId);
        idMsg.setUnderlined(true);
        idMsg.setItalic(true);
        idMsg.setColor(ChatColor.GRAY);
        idMsg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(clickToCopy)));
        idMsg.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, sfId));
        sender.spigot().sendMessage(msg, idMsg);
    }
}
