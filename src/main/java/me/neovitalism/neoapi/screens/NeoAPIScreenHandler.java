package me.neovitalism.neoapi.screens;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;

public class NeoAPIScreenHandler extends ScreenHandler {
    protected NeoAPIScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);

    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return false;
    }
}
