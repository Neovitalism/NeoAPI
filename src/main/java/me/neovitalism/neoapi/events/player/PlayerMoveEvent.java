package me.neovitalism.neoapi.events.player;

import me.neovitalism.neoapi.objects.Location;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerMoveEvent {
    Event<PlayerMoveEvent> EVENT = EventFactory.createArrayBacked(PlayerMoveEvent.class, (listeners) -> (player, oldLocation, newLocation) -> {
        for (PlayerMoveEvent listener : listeners) {
            Location alteredLocation = listener.interact(player, oldLocation, newLocation);
            if(!alteredLocation.isEqualTo(newLocation)) {
                return alteredLocation;
            }
        }
        return newLocation;
    });

    Location interact(ServerPlayerEntity player, Location oldLocation, Location newLocation);
}
