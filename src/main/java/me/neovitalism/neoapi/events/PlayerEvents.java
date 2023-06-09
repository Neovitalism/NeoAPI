package me.neovitalism.neoapi.events;

import me.neovitalism.neoapi.objects.Location;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class PlayerEvents {
    public static final Event<EventInterfaces.PlayerJoinEvent> JOIN = EventFactory.createArrayBacked(
            EventInterfaces.PlayerJoinEvent.class, (listeners) -> (player, firstJoin) -> {
                for (EventInterfaces.PlayerJoinEvent listener : listeners) {
                    listener.interact(player, firstJoin);
                }
            });

    public static final Event<EventInterfaces.PlayerMoveEvent> MOVE = EventFactory.createArrayBacked(
            EventInterfaces.PlayerMoveEvent.class, (listeners) -> (player, oldLocation, newLocation) -> {
                for (EventInterfaces.PlayerMoveEvent listener : listeners) {
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
    public static final Event<EventInterfaces.PlayerOffhandSwapEvent> OFFHAND_SWAP = EventFactory.createArrayBacked(
            EventInterfaces.PlayerOffhandSwapEvent.class, (listeners) -> (player, mainHandItem, offHandItem) -> {
                for (EventInterfaces.PlayerOffhandSwapEvent listener : listeners) {
                    if(!listener.interact(player, mainHandItem, offHandItem)) return false;
                }
                return true;
            });

    public static final Event<EventInterfaces.PlayerInteractEntityEvent> INTERACT_ENTITY = EventFactory.createArrayBacked(
            EventInterfaces.PlayerInteractEntityEvent.class, (listeners) -> (player, entity, interactType, usedHand, shifting) -> {
                for(EventInterfaces.PlayerInteractEntityEvent listener : listeners) {
                    if(!listener.interact(player, entity, interactType, usedHand, shifting)) return false;
                }
                return true;
            });

    public static final Event<EventInterfaces.PlayerDropItemEvent> DROP_ITEMS = EventFactory.createArrayBacked(
            EventInterfaces.PlayerDropItemEvent.class, (listeners) -> (player, dropped, wholeStack) -> {
                for(EventInterfaces.PlayerDropItemEvent listener : listeners) {
                    if(!listener.interact(player, dropped, wholeStack)) return false;
                }
                return true;
            });

    public static final Event<EventInterfaces.PlayerInteractBlockEvent> INTERACT_BLOCK = EventFactory.createArrayBacked(
            EventInterfaces.PlayerInteractBlockEvent.class, (listeners) -> (player, serverWorld, blockPos, hand) -> {
                for(EventInterfaces.PlayerInteractBlockEvent listener : listeners) {
                    if(!listener.interact(player, serverWorld, blockPos, hand)) return false;
                }
                return true;
            });

    public static final Event<EventInterfaces.PlayerInteractItemEvent> INTERACT_ITEM = EventFactory.createArrayBacked(
            EventInterfaces.PlayerInteractItemEvent.class, (listeners) -> (player, hand) -> {
                for(EventInterfaces.PlayerInteractItemEvent listener : listeners) {
                    if(!listener.interact(player, hand)) return false;
                }
                return true;
            });

    public static final Event<EventInterfaces.PlayerChatEvent> CHAT = EventFactory.createArrayBacked(
            EventInterfaces.PlayerChatEvent.class, (listeners) -> (player, message) -> {
                for(EventInterfaces.PlayerChatEvent listener : listeners) {
                    String newMessage = listener.interact(player, message);
                    if(!message.equals(newMessage)) return message;
                }
                return message;
            });

    public static final Event<EventInterfaces.PlayerCommandEvent> RUN_COMMAND = EventFactory.createArrayBacked(
            EventInterfaces.PlayerCommandEvent.class, (listeners) -> (player, command) -> {
                for(EventInterfaces.PlayerCommandEvent listener : listeners) {
                    if(!listener.interact(player, command)) return false;
                }
                return true;
            });

    public static final Event<EventInterfaces.PlayerSwingHandEvent> SWING_HAND = EventFactory.createArrayBacked(
            EventInterfaces.PlayerSwingHandEvent.class, (listeners) -> (player, hand) -> {
                for(EventInterfaces.PlayerSwingHandEvent listener : listeners) {
                    if(!listener.interact(player, hand)) return false;
                }
                return true;
            });

    public static final Event<EventInterfaces.PlayerPaddleBoatEvent> PADDLE_BOAT = EventFactory.createArrayBacked(
            EventInterfaces.PlayerPaddleBoatEvent.class, (listeners) -> (player, boatEntity) -> {
                for(EventInterfaces.PlayerPaddleBoatEvent listener : listeners) {
                    if(!listener.interact(player, boatEntity)) return false;
                }
                return true;
            });
}
