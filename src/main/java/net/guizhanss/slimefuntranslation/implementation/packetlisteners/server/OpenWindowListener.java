package net.guizhanss.slimefuntranslation.implementation.packetlisteners.server;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class OpenWindowListener extends AServerListener {
    public OpenWindowListener() {
        super(PacketType.Play.Server.OPEN_WINDOW);
    }

    @Override
    protected void process(@Nonnull PacketEvent event) {
        var user = getUser(event);
        if (user == null) {
            return;
        }
        var block = user.getRecentClickedBlock();
        if (block == null) {
            return;
        }

        final PacketContainer packet = event.getPacket();
        StructureModifier<WrappedChatComponent> modifier = packet.getChatComponents();
        WrappedChatComponent wrappedChatComponent = modifier.read(0);
        BaseComponent[] components = ComponentSerializer.parse(wrappedChatComponent.getJson());
        String title = Stream.of(components).map(c -> c.toLegacyText()).collect(Collectors.joining());
        if (!block.getItemName().equals(title)) {
            return;
        }
        modifier.write(0, WrappedChatComponent.fromLegacyText(
            SlimefunTranslation.getTranslationService().getTranslatedItemName(user, block)
        ));
    }
}
