package me.neovitalism.neoapi.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public interface JoinEvent {
    Event<JoinEvent> EVENT = EventFactory.createArrayBacked(JoinEvent.class, (listeners) -> (player, firstJoin) -> {
        for (JoinEvent listener : listeners) {
            listener.interact(player, firstJoin);
        }
    });

    void interact(ServerPlayerEntity player, boolean firstJoin);
}
