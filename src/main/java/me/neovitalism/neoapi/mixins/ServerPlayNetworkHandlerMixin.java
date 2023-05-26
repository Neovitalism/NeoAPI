package me.neovitalism.neoapi.mixins;

import me.neovitalism.neoapi.events.player.PlayerMoveEvent;
import me.neovitalism.neoapi.objects.Location;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayerEntity player;

    @Shadow public abstract void requestTeleport(double x, double y, double z, float yaw, float pitch);

    @Inject(method = "onPlayerMove(Lnet/minecraft/network/packet/c2s/play/PlayerMoveC2SPacket;)V", at = @At(value = "HEAD"), cancellable = true)
    public void neoAPI$onPlayerMove(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        Location oldLocation = new Location(player.getWorld(), player.getX(), player.getY(), player.getZ(), player.getPitch(), player.getYaw());
        double x = MathHelper.clamp(packet.getX(this.player.getX()), -3.0E7D, 3.0E7D);
        double y = MathHelper.clamp(packet.getY(this.player.getY()), -2.0E7D, 2.0E7D);
        double z = MathHelper.clamp(packet.getZ(this.player.getZ()), -3.0E7D, 3.0E7D);
        float yaw = MathHelper.wrapDegrees(packet.getYaw(this.player.getYaw()));
        float pitch = MathHelper.wrapDegrees(packet.getPitch(this.player.getPitch()));
        Location newLocation = new Location(player.getWorld(), x, y, z, pitch, yaw);
        Location alteredLocation = PlayerMoveEvent.EVENT.invoker().interact(player, oldLocation, newLocation);
        if(!alteredLocation.isEqualTo(newLocation)) {
            alteredLocation.teleport(player);
            ci.cancel();
        }
    }
}
