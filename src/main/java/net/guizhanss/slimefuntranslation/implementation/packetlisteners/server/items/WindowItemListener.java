package net.guizhanss.slimefuntranslation.implementation.packetlisteners.server.items;

import java.util.List;

import javax.annotation.Nonnull;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;

import org.bukkit.inventory.ItemStack;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.implementation.packetlisteners.server.AServerListener;

public class WindowItemListener extends AServerListener {
    public WindowItemListener() {
        super(PacketType.Play.Server.WINDOW_ITEMS);
    }

    @Override
    protected void process(@Nonnull PacketEvent event) {
        var user = getUser(event);
        if (user == null) {
            return;
        }
        PacketContainer packet = event.getPacket();
        StructureModifier<List<ItemStack>> modifier = packet.getItemListModifier();
        List<ItemStack> items = modifier.read(0);
        for (ItemStack item : items) {
            SlimefunTranslation.getTranslationManager().translateItem(user, item);
        }
        modifier.write(0, items);
    }
}
