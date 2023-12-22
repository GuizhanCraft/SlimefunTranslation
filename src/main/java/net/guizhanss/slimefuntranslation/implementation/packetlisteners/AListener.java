package net.guizhanss.slimefuntranslation.implementation.packetlisteners;

import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.base.Preconditions;

import org.bukkit.entity.Player;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.core.users.User;

public abstract class AListener {
    protected final PacketType packetType;
    protected PacketAdapter adapter;

    protected AListener(@Nonnull PacketType packetType) {
        this.packetType = packetType;
    }

    @Nullable
    public User getUser(@Nonnull PacketEvent event) {
        Preconditions.checkArgument(event != null, "PacketEvent cannot be null");
        Player p = event.getPlayer();
        if (event.isPlayerTemporary()) {
            SlimefunTranslation.log(Level.WARNING,
                "ProtocolLib returns temporary player [{0}] for packet {1}. It cannot be processed.",
                p.getAddress(), packetType.name());
            return null;
        } else {
            return SlimefunTranslation.getUserManager().getUser(p);
        }
    }

    public void register() {
        ProtocolLibrary.getProtocolManager().addPacketListener(adapter);
    }

    protected abstract void process(@Nonnull PacketEvent event);
}
