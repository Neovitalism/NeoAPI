package me.neovitalism.neoapi.utils;

import me.neovitalism.neoapi.NeoAPI;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Formatting;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GlowUtil {
    private static final Map<Formatting, GlowTeam> GLOW_TEAMS = new HashMap<>();
    private static Constructor<TeamS2CPacket> packetConstructor;

    public static void glow(Entity entity, Formatting color) {
        GlowTeam team = GlowUtil.GLOW_TEAMS.get(color);
        if (team != null) team.forAll(entity);
        entity.setGlowing(true);
    }

    public static void removeGlow(Entity entity) {
        entity.setGlowing(false);
    }
//
//    public static void glow(ServerPlayerEntity viewer, Entity entity, Formatting color) {
//        GlowTeam team = GlowUtil.GLOW_TEAMS.get(color);
//        if (team != null) team.forViewer(viewer, entity);
//        viewer.networkHandler.sendPacket(new EntityStatusS2CPacket(entity, (byte) 24));
//    }

    private static class GlowTeam {
        private final Formatting color;

        public GlowTeam(Formatting color) {
            this.color = color;
        }

        public void forAll(Entity entity) {
            NeoAPI.getServer().getPlayerManager().sendToAll(this.getTeamCreatePacket(entity.getNameForScoreboard()));
        }

        public void forViewer(ServerPlayerEntity viewer, Entity entity) {
            viewer.networkHandler.sendPacket(this.getTeamCreatePacket(entity.getNameForScoreboard()));
        }

        private TeamS2CPacket getTeamCreatePacket(String entityName) {
            RegistryByteBuf buf = new RegistryByteBuf(PacketByteBufs.create(), NeoAPI.getServer().getRegistryManager());
            buf.writeString(UUID.randomUUID().toString());
            buf.writeByte(0);
            TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.encode(buf, Text.empty());
            buf.writeByte(3);
            buf.writeString("always");
            buf.writeString("always");
            buf.writeEnumConstant(this.color);
            TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.encode(buf, Text.empty());
            TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.encode(buf, Text.empty());
            buf.writeCollection(List.of(entityName), PacketByteBuf::writeString);
            try {
                return GlowUtil.packetConstructor.newInstance(buf);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    static {
        for (Formatting value : Formatting.values()) {
            if (value.isColor()) GlowUtil.GLOW_TEAMS.put(value, new GlowTeam(value));
        }
        try {
            GlowUtil.packetConstructor = TeamS2CPacket.class.getDeclaredConstructor(RegistryByteBuf.class);
            GlowUtil.packetConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
