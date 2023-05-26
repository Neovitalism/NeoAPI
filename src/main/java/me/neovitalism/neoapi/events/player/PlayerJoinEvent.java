package me.neovitalism.neoapi.events.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerJoinEvent {
    Event<PlayerJoinEvent> EVENT = EventFactory.createArrayBacked(PlayerJoinEvent.class, (listeners) -> (player, firstJoin) -> {
        for (PlayerJoinEvent listener : listeners) {
            listener.interact(player, firstJoin);
        }
    });

    void interact(ServerPlayerEntity player, boolean firstJoin);
}
