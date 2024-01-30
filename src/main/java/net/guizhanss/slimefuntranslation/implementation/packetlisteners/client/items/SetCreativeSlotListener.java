package net.guizhanss.slimefuntranslation.implementation.packetlisteners.client.items;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.implementation.packetlisteners.client.AClientListener;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class SetCreativeSlotListener extends AClientListener {
    public SetCreativeSlotListener() {
        super(PacketType.Play.Client.SET_CREATIVE_SLOT);
    }

    protected void process(@Nonnull PacketEvent event) {
        var user = getUser(event);
        if (user == null) {
            return;
        }
        ItemStack item = event.getPacket().getItemModifier().read(0);
        SlimefunTranslation.getTranslationService().translateItem(user, item);
    }
}
