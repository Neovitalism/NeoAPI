package me.neovitalism.neoapi.helpers;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Locale;

public class ItemStackHelper {
    public static Identifier getIdentifier(Item item) {
        return Registry.ITEM.getId(item);
    }

    public static Identifier getIdentifier(ItemStack item) {
        return getIdentifier(item.getItem());
    }

    public static String toMiniItemHover(ItemStack item) {
        NbtCompound itemNBT = item.getNbt();
        if(itemNBT != null) {
            return "<hover:show_item:"
                    + item.getItem().getName().getString()
                    .toLowerCase(Locale.ENGLISH)
                    .replace(" ", "_") +
                    ":" + item.getCount() + ":'" +
                    itemNBT + "'>";
        } else {
            return "<hover:show_item:"
                    + item.getItem().getName().getString()
                    .toLowerCase(Locale.ENGLISH)
                    .replace(" ", "_")
                    + ":" + item.getCount() + ">";
        }
    }
}
