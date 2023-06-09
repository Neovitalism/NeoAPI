package me.neovitalism.neoapi.mixins;

import me.neovitalism.neoapi.events.EventInterfaces;
import me.neovitalism.neoapi.events.PlayerEvents;
import me.neovitalism.neoapi.objects.Location;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayerEntity player;

    @Inject(method = "onPlayerMove",
            at = @At(value = "HEAD"), cancellable = true)
    public void neoAPI$onPlayerMove(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        Location oldLocation = new Location(player.getWorld(), player.getX(), player.getY(), player.getZ(), player.getPitch(), player.getYaw());
        double x = MathHelper.clamp(packet.getX(this.player.getX()), -3.0E7D, 3.0E7D);
        double y = MathHelper.clamp(packet.getY(this.player.getY()), -2.0E7D, 2.0E7D);
        double z = MathHelper.clamp(packet.getZ(this.player.getZ()), -3.0E7D, 3.0E7D);
        float yaw = MathHelper.wrapDegrees(packet.getYaw(this.player.getYaw()));
        float pitch = MathHelper.wrapDegrees(packet.getPitch(this.player.getPitch()));
        Location newLocation = new Location(player.getWorld(), x, y, z, pitch, yaw);
        Location alteredLocation = PlayerEvents.MOVE.invoker().interact(player, oldLocation, newLocation);
        if(!alteredLocation.isEqualTo(newLocation)) {
            alteredLocation.teleport(player);
            ci.cancel();
        }
    }

    @Inject(method = "onPlayerAction",
            at = @At(target = "Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket;getAction()Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket$Action;"
                , value = "INVOKE"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    public void neoAPI$onPlayerAction(PlayerActionC2SPacket packet, CallbackInfo ci, BlockPos blockPos) {
        PlayerActionC2SPacket.Action packetAction = packet.getAction();
        if(!player.isSpectator()) {
            switch (packetAction) {
                case SWAP_ITEM_WITH_OFFHAND:
                    if (!PlayerEvents.OFFHAND_SWAP.invoker().interact(player, player.getStackInHand(Hand.MAIN_HAND), player.getStackInHand(Hand.OFF_HAND))) {
                        ci.cancel();
                    }
                    break;
                case DROP_ITEM:
                case DROP_ALL_ITEMS:
                    if (!PlayerEvents.DROP_ITEMS.invoker().interact(player, player.getStackInHand(Hand.MAIN_HAND), packetAction == PlayerActionC2SPacket.Action.DROP_ALL_ITEMS)) {
                        ci.cancel();
                    }
            }
        }
    }

    @Inject(method = "onPlayerInteractEntity",
            at = @At(target = "Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket;handle(Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket$Handler;)V",
                    value = "INVOKE"), cancellable = true)
    public void neoAPI$onInteractEntity(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) {
        Entity entity = packet.getEntity(player.getWorld());
        if(entity != null) {
            final EventInterfaces.PlayerInteractEntityEvent.InteractType[] interactType = new EventInterfaces.PlayerInteractEntityEvent.InteractType[1];
            final Hand[] usedHand = new Hand[1];
            packet.handle(new PlayerInteractEntityC2SPacket.Handler() {
                @Override
                public void interact(Hand hand) {
                    usedHand[0] = hand;
                    interactType[0] = EventInterfaces.PlayerInteractEntityEvent.InteractType.INTERACT;
                }

                @Override
                public void interactAt(Hand hand, Vec3d pos) {
                    usedHand[0] = hand;
                    interactType[0] = EventInterfaces.PlayerInteractEntityEvent.InteractType.INTERACT_AT;
                }

                @Override
                public void attack() {
                    usedHand[0] = Hand.MAIN_HAND;
                    interactType[0] = EventInterfaces.PlayerInteractEntityEvent.InteractType.ATTACK;
                }
            });
            if (!PlayerEvents.INTERACT_ENTITY.invoker().interact(player, entity, interactType[0], usedHand[0], packet.isPlayerSneaking())) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onPlayerInteractBlock",
            at = @At(target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;interactBlock(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;",
                    value = "INVOKE"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    public void neoAPI$onInteractBlock(PlayerInteractBlockC2SPacket packet, CallbackInfo ci, ServerWorld serverWorld, Hand hand, ItemStack itemStack, BlockHitResult blockHitResult, Vec3d vec3d, BlockPos blockPos, Vec3d vec3d2, Vec3d vec3d3, double d, Direction direction, int i) {
        if(!PlayerEvents.INTERACT_BLOCK.invoker().interact(player, serverWorld, blockPos, hand)) {
            ci.cancel();
        }
    }

    @Inject(method = "onPlayerInteractItem",
            at = @At(target = "Lnet/minecraft/server/network/ServerPlayerEntity;updateLastActionTime()V",
                    value = "TAIL"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    public void neoAPI$onInteractItem(PlayerInteractItemC2SPacket packet, CallbackInfo ci, ServerWorld serverWorld, Hand hand, ItemStack itemStack) {
        if(!PlayerEvents.INTERACT_ITEM.invoker().interact(player, hand)) {
            ci.cancel();
        }
    }

    @Inject(method = "onChatMessage",
            at = @At(target = "Lnet/minecraft/server/MinecraftServer;submit(Ljava/lang/Runnable;)Ljava/util/concurrent/CompletableFuture;",
                    value = "INVOKE"), cancellable = true)
    public void neoAPI$onChat(ChatMessageC2SPacket packet, CallbackInfo ci) {
        String message = PlayerEvents.CHAT.invoker().interact(player, packet.chatMessage());
        if(message == null) {
            ci.cancel();
        } else if(!message.equals(packet.chatMessage())) {
            // fix this later, cba fucking with this mixin rn
        }
    }

    @Inject(method = "onCommandExecution",
            at = @At(target = "Lnet/minecraft/server/MinecraftServer;submit(Ljava/lang/Runnable;)Ljava/util/concurrent/CompletableFuture;",
                    value = "INVOKE"), cancellable = true)
    public void neoAPI$onCommand(CommandExecutionC2SPacket packet, CallbackInfo ci) {
        if(!PlayerEvents.RUN_COMMAND.invoker().interact(player, packet.command())) {
            ci.cancel();
        }
    }

    @Inject(method = "onHandSwing",
            at = @At(target = "Lnet/minecraft/server/network/ServerPlayerEntity;swingHand(Lnet/minecraft/util/Hand;)V",
                    value = "INVOKE"), cancellable = true)
    public void neoAPI$onHandSwing(HandSwingC2SPacket packet, CallbackInfo ci) {
        if(!PlayerEvents.SWING_HAND.invoker().interact(player, packet.getHand())) {
            ci.cancel();
        }
    }

    @Inject(method = "onBoatPaddleState",
            at = @At(target = "Lnet/minecraft/entity/vehicle/BoatEntity;setPaddleMovings(ZZ)V",
                    value = "INVOKE"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    public void neoAPI$onBoatPaddle(BoatPaddleStateC2SPacket packet, CallbackInfo ci, Entity entity) {
        if(!PlayerEvents.PADDLE_BOAT.invoker().interact(player, (BoatEntity) entity)) {
            ci.cancel();
        }
    }
}
