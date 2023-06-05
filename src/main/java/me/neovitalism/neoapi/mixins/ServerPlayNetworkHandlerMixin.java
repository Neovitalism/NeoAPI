package me.neovitalism.neoapi.mixins;

import me.neovitalism.neoapi.events.PlayerEvents;
import me.neovitalism.neoapi.objects.Location;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.server.network.ServerPlayNetworkHandler.MAX_BREAK_SQUARED_DISTANCE;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayerEntity player;

    @Shadow public abstract void requestTeleport(double x, double y, double z, float yaw, float pitch);

    @Inject(method = "onPlayerMove(Lnet/minecraft/network/packet/c2s/play/PlayerMoveC2SPacket;)V",
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

    @Inject(method = "onPlayerAction(Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket;)V",
            at = @At(value = "HEAD"), cancellable = true)
    public void neoAPI$onPlayerAction(PlayerActionC2SPacket packet, CallbackInfo ci) {

    }

    @Inject(method = "onPlayerInteractEntity(Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket;)V",
            at = @At(value = "HEAD"), cancellable = true)
    public void neoAPI$onInteractEntity(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) {
        Entity entity = packet.getEntity(player.getWorld());
        if(entity != null) {
            if (entity.squaredDistanceTo(this.player.getEyePos()) < MAX_BREAK_SQUARED_DISTANCE) {
                final PlayerEvents.PlayerInteractEntityEvent.InteractType[] interactType = new PlayerEvents.PlayerInteractEntityEvent.InteractType[1];
                final Hand[] usedHand = new Hand[1];
                packet.handle(new PlayerInteractEntityC2SPacket.Handler() {
                    @Override
                    public void interact(Hand hand) {
                        usedHand[0] = hand;
                        interactType[0] = PlayerEvents.PlayerInteractEntityEvent.InteractType.INTERACT;
                    }

                    @Override
                    public void interactAt(Hand hand, Vec3d pos) {
                        usedHand[0] = hand;
                        interactType[0] = PlayerEvents.PlayerInteractEntityEvent.InteractType.INTERACT_AT;
                    }

                    @Override
                    public void attack() {
                        usedHand[0] = Hand.MAIN_HAND;
                        interactType[0] = PlayerEvents.PlayerInteractEntityEvent.InteractType.ATTACK;
                    }
                });
                if (!PlayerEvents.INTERACT_ENTITY.invoker().interact(player, entity, interactType[0], usedHand[0], packet.isPlayerSneaking())) {
                    ci.cancel();
                }
            }
        }
    }
}
