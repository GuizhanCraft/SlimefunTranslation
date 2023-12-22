package net.guizhanss.slimefuntranslation.implementation.packetlisteners.server;

import javax.annotation.Nonnull;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.implementation.packetlisteners.AListener;

public abstract class AServerListener extends AListener {
    protected AServerListener(@Nonnull PacketType packetType) {
        super(packetType);

        adapter = new PacketAdapter(SlimefunTranslation.getInstance(), ListenerPriority.HIGHEST, packetType) {
            @Override
            public void onPacketSending(@Nonnull PacketEvent event) {
                process(event);
            }
        };
    }
}
