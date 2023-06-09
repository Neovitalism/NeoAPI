package me.neovitalism.neoapi.events;

import me.neovitalism.neoapi.objects.Location;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class EventInterfaces {
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

    public interface PlayerDropItemEvent {
        boolean interact(ServerPlayerEntity player, ItemStack dropped, boolean wholeStack);
    }

    public interface PlayerInteractBlockEvent {
        boolean interact(ServerPlayerEntity player, ServerWorld world, BlockPos blockPos, Hand hand);
    }

    public interface PlayerInteractItemEvent {
        boolean interact(ServerPlayerEntity player, Hand hand);
    }

    public interface PlayerChatEvent {
        String interact(ServerPlayerEntity player, String message);
    }

    public interface PlayerCommandEvent {
        boolean interact(ServerPlayerEntity player, String command);
    }
}
