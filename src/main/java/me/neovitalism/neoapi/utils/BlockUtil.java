package me.neovitalism.neoapi.utils;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class BlockUtil {
    public static boolean isWater(BlockState state) {
        return state.isOf(Blocks.WATER);
    }

    public static boolean isAir(BlockState state) {
        return state.isAir();
    }
}
