package net.guizhanss.slimefuntranslation.implementation.packetlisteners.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;
import net.guizhanss.slimefuntranslation.core.users.User;
import net.guizhanss.slimefuntranslation.utils.SlimefunItemUtils;

public class EntityMetadataListener extends AServerListener {

    private static final boolean USE_DV = Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_20) ||
        (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_19) && PaperLib.getMinecraftPatchVersion() >= 3);

    public EntityMetadataListener() {
        super(PacketType.Play.Server.ENTITY_METADATA);
    }

    @Override
    protected void process(@Nonnull PacketEvent event) {
        var user = getUser(event);
        if (user == null) {
            return;
        }
        final PacketContainer packet = event.getPacket();
        final Entity entity = packet.getEntityModifier(event).read(0);

        if (entity == null || entity.getType() != EntityType.DROPPED_ITEM) {
            return;
        }

        final Item itemEntity = (Item) entity;
        if (!entity.isCustomNameVisible()) {
            return;
        }
        final ItemStack item = itemEntity.getItemStack();
        final String sfId = SlimefunItemUtils.getId(item);
        if (sfId == null) {
            return;
        }
        PacketContainer processed = processPacket(packet, user, itemEntity, sfId);
        event.setPacket(processed);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    private PacketContainer processPacket(PacketContainer packet, User user, Item item, String sfId) {
        final WrappedDataWatcher dataWatcher = WrappedDataWatcher.getEntityWatcher(item).deepClone();
        final WrappedDataWatcherObject optChatFieldWatcher = new WrappedDataWatcherObject(2, Registry.getChatComponentSerializer(true));
        final Optional<Object> optChatField = Optional.of(WrappedChatComponent.fromChatMessage(
            SlimefunTranslation.getTranslationService().getTranslatedItemName(user, SlimefunItem.getById(sfId))
        )[0].getHandle());
        dataWatcher.setObject(optChatFieldWatcher, optChatField);
        if (USE_DV) {
            final List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();
            for (final WrappedWatchableObject entry : dataWatcher.getWatchableObjects()) {
                if (entry == null) continue;
                final WrappedDataWatcherObject watcherObject = entry.getWatcherObject();
                wrappedDataValueList.add(
                    new WrappedDataValue(
                        watcherObject.getIndex(),
                        watcherObject.getSerializer(),
                        entry.getRawValue()
                    )
                );
            }
            packet.getDataValueCollectionModifier().write(0, wrappedDataValueList);
        } else {
            packet.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());
        }
        return packet;
    }
}
