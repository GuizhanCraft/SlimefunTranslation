package net.guizhanss.slimefuntranslation.implementation.managers;

import java.util.ArrayList;
import java.util.List;

import net.guizhanss.slimefuntranslation.implementation.packetlisteners.AListener;
import net.guizhanss.slimefuntranslation.implementation.packetlisteners.client.items.SetCreativeSlotListener;
import net.guizhanss.slimefuntranslation.implementation.packetlisteners.client.items.WindowClickListener;
import net.guizhanss.slimefuntranslation.implementation.packetlisteners.server.items.SetSlotListener;
import net.guizhanss.slimefuntranslation.implementation.packetlisteners.server.items.WindowItemListener;

public final class PacketListenerManager {

    public PacketListenerManager() {
        List<AListener> packetListeners = new ArrayList<>();

        packetListeners.add(new SetSlotListener());
        packetListeners.add(new WindowItemListener());
        packetListeners.add(new SetCreativeSlotListener());
        packetListeners.add(new WindowClickListener());

        for (var listener : packetListeners) {
            listener.register();
        }
    }
}
