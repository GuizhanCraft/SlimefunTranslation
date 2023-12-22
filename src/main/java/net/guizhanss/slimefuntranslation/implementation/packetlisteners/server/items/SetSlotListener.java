package net.guizhanss.slimefuntranslation.implementation.packetlisteners.server.items;

import javax.annotation.Nonnull;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;

import org.bukkit.inventory.ItemStack;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.implementation.packetlisteners.server.AServerListener;

public class SetSlotListener extends AServerListener {
    public SetSlotListener() {
        super(PacketType.Play.Server.SET_SLOT);
    }

    @Override
    protected void process(@Nonnull PacketEvent event) {
        var user = getUser(event);
        if (user == null) {
            return;
        }
        PacketContainer packet = event.getPacket();
        StructureModifier<ItemStack> modifier = packet.getItemModifier();
        for (int i = 0; i < modifier.size(); i++) {
            ItemStack item = modifier.read(i);
            if (SlimefunTranslation.getTranslationManager().translateItem(user, item)) {
                modifier.write(i, item);
            }
        }
    }
}
