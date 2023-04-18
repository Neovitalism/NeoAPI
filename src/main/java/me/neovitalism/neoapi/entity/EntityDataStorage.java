package me.neovitalism.neoapi.entity;

import net.minecraft.nbt.NbtCompound;

public interface EntityDataStorage {
    NbtCompound getPersistentData();
}
