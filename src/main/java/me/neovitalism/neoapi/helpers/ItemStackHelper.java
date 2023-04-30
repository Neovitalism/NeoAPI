package me.neovitalism.neoapi.helpers;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemStackHelper {
    public static Identifier getIdentifier(Item item) {
        return Registry.ITEM.getId(item);
    }

    public static Identifier getIdentifier(ItemStack item) {
        return getIdentifier(item.getItem());
    }
}
