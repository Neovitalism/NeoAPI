package me.neovitalism.neoapi.events;

import me.neovitalism.neoapi.objects.Location;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

public class PlayerEvents {
    public static final Event<PlayerJoinEvent> JOIN = EventFactory.createArrayBacked(PlayerJoinEvent.class, (listeners) -> (player, firstJoin) -> {
        for (PlayerJoinEvent listener : listeners) {
            listener.interact(player, firstJoin);
        }
    });

    public static final Event<PlayerMoveEvent> MOVE = EventFactory.createArrayBacked(PlayerMoveEvent.class, (listeners) -> (player, oldLocation, newLocation) -> {
        for (PlayerMoveEvent listener : listeners) {
            Location alteredLocation = listener.interact(player, oldLocation, newLocation);
            if(!alteredLocation.isEqualTo(newLocation)) {
                return alteredLocation;
            }
        }
        return newLocation;
    });

    /**
     * This event fires before the swap takes place. Returning false will cancel the event.
     */
    public static final Event<PlayerOffhandSwapEvent> OFFHAND_SWAP = EventFactory.createArrayBacked(PlayerOffhandSwapEvent.class, (listeners) -> (player, mainHandItem, offHandItem) -> {
        for (PlayerOffhandSwapEvent listener : listeners) {
            if(!listener.interact(player, mainHandItem, offHandItem)) return false;
        }
        return true;
    });

    public static final Event<PlayerInteractEntityEvent> INTERACT_ENTITY = EventFactory.createArrayBacked(PlayerInteractEntityEvent.class, (listeners) -> (player, entity, interactType, usedHand, shifting) -> {
        for(PlayerInteractEntityEvent listener : listeners) {
            if(!listener.interact(player, entity, interactType, usedHand, shifting)) return false;
        }
        return true;
    });

    public interface PlayerJoinEvent {
        void interact(ServerPlayerEntity player, boolean firstJoin);
    }

    public interface PlayerMoveEvent {
        Location interact(ServerPlayerEntity player, Location oldLocation, Location newLocation);
    }

    public interface PlayerOffhandSwapEvent {
        boolean interact(ServerPlayerEntity player, ItemStack mainHandItem, ItemStack offHandItem);
    }

    public interface PlayerInteractEntityEvent {
        boolean interact(ServerPlayerEntity player, Entity entity, InteractType interactType, Hand usedHand, boolean shifting);

        enum InteractType {
            ATTACK,
            INTERACT,
            INTERACT_AT
        }
    }
}
