package me.neovitalism.neoapi.scoreboard;

import me.neovitalism.neoapi.NeoAPI;
import me.neovitalism.neoapi.player.PlayerManager;
import me.neovitalism.neoapi.utils.ColorUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreUpdateS2CPacket;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.BlankNumberFormat;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class NeoScoreboard {
    private final String id;
    private final ScoreboardObjective obj;

    private final List<UUID> viewingUUIDs = new ArrayList<>();

    private List<Text> text;

    public NeoScoreboard(String id, String title, List<String> text) {
        this.id = id;
        this.obj = new ScoreboardObjective(
                NeoAPI.getServer().getScoreboard(),
                id,
                ScoreboardCriterion.DUMMY,
                ColorUtil.parseColourToText(title),
                ScoreboardCriterion.RenderType.INTEGER,
                false,
                BlankNumberFormat.INSTANCE);
        this.setText(text);
    }

    public void setText(List<String> text) {
        this.text = text.stream().map(ColorUtil::parseColourToText).toList();
        this.updateAllViewers();
    }

    public void send(ServerPlayerEntity player) {
        this.remove(player);
        this.send(player, new ScoreboardObjectiveUpdateS2CPacket(this.obj, ScoreboardObjectiveUpdateS2CPacket.ADD_MODE));

        final int size = this.text.size();
        for (int i = 0; i < size; i++) {
            Text line = this.text.get(i);
            this.send(player, new ScoreboardScoreUpdateS2CPacket(String.valueOf(i),
                    this.id, size - i, Optional.of(line), Optional.of(BlankNumberFormat.INSTANCE)));
        }

        this.send(player, new ScoreboardDisplayS2CPacket(ScoreboardDisplaySlot.SIDEBAR, this.obj));
        this.viewingUUIDs.add(player.getUuid());
    }

    private void send(ServerPlayerEntity player, Packet<?> packet) {
        player.networkHandler.sendPacket(packet);
    }

    public void remove(ServerPlayerEntity player) {
        this.send(player, new ScoreboardObjectiveUpdateS2CPacket(this.obj, ScoreboardObjectiveUpdateS2CPacket.REMOVE_MODE));
        this.viewingUUIDs.remove(player.getUuid());
    }

    public void delete() {
        this.collectViewers().forEach(this::remove);
    }

    private void updateAllViewers() {
        this.collectViewers().forEach(this::send);
    }

    private List<ServerPlayerEntity> collectViewers() {
        List<ServerPlayerEntity> players = new ArrayList<>();
        for (UUID playerUUID : this.viewingUUIDs) {
            ServerPlayerEntity player = PlayerManager.getPlayer(playerUUID);
            if (player != null) players.add(player);
        }
        return players;
    }
}
