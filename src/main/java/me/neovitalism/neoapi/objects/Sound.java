package me.neovitalism.neoapi.objects;

import me.neovitalism.neoapi.config.Configuration;
import me.neovitalism.neoapi.player.PlayerManager;
import me.neovitalism.neoapi.utils.ServerUtil;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.Locale;

public class Sound {
    private final Identifier identifier;
    private final SoundCategory category;

    private final float volume;
    private final float pitch;

    private final Location origin;
    private final float distance;

    public Sound(Configuration config) {
        this.identifier = config.getIdentifier("identifier");
        this.category = SoundCategory.valueOf(config.getString("category", "master").toUpperCase(Locale.ENGLISH));
        this.volume = config.getFloat("volume", 1.0F);
        this.pitch = config.getFloat("pitch", 1.0F);
        this.origin = config.getLocation("origin");
        this.distance = config.getFloat("distance", 16.0F);
    }

    public void sendToPlayer(ServerPlayerEntity player) {
        ServerUtil.executeSync(() -> player.networkHandler.sendPacket(this.buildPacket(player)));
    }

    public void sendToAll() {
        ServerUtil.executeSync(() -> {
            for (ServerPlayerEntity player : PlayerManager.getOnlinePlayers()) {
                player.networkHandler.sendPacket(this.buildPacket(player));
            }
        });
    }

    private PlaySoundS2CPacket buildPacket(ServerPlayerEntity player) {
        SoundEvent sound = SoundEvent.of(this.identifier, this.distance);
        Location origin = (this.origin == null) ? new Location(player) : this.origin;
        return new PlaySoundS2CPacket(RegistryEntry.of(sound), this.category,
                origin.getX(), origin.getY(), origin.getZ(),
                this.volume, this.pitch, player.getRandom().nextLong());
    }

}
