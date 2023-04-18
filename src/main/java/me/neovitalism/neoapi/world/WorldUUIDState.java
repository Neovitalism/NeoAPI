package me.neovitalism.neoapi.world;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import java.util.UUID;

public class WorldUUIDState extends PersistentState {
    private UUID worldUUID;

    public WorldUUIDState() {
        this.worldUUID = UUID.randomUUID();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putUuid("worldUUID", worldUUID);
        return nbt;
    }

    public static WorldUUIDState createFromNbt(NbtCompound nbt) {
        WorldUUIDState worldUUIDState = new WorldUUIDState();
        worldUUIDState.worldUUID = nbt.getUuid("worldUUID");
        return worldUUIDState;
    }

    protected static UUID getOrCreateWorldUUID(ServerWorld world) {
        WorldUUIDState worldUUIDState = world.getPersistentStateManager()
                .getOrCreate(WorldUUIDState::createFromNbt,
                        WorldUUIDState::new,
                        "worldUUIDState");
        worldUUIDState.markDirty();
        return worldUUIDState.worldUUID;
    }
}
