package me.neovitalism.neoapi.helpers;

import me.neovitalism.neoapi.modloading.config.Configuration;
import me.neovitalism.neoapi.utils.ColorUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
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

    public static ItemStack fromConfig(Configuration itemConfig, int amount) {
        Item item = Registry.ITEM.get(Identifier.tryParse(itemConfig.getString("Material")));
        if(item != Items.AIR) {
            ItemStack itemStack = new ItemStack(item);
            String name = itemConfig.getString("Name");
            if(name != null) itemStack.setCustomName(ColorUtil.parseColourToText(name));
            List<String> lore = itemConfig.getStringList("Lore");
            setLore(itemStack, lore);
            itemStack.setCount((amount == -1) ? itemConfig.getInt("Amount", 1) : amount);
            List<String> enchants = itemConfig.getStringList("Enchants");
            for(String enchant : enchants) {
                String[] enchantParsed = enchant.split(" ");
                if(enchantParsed.length != 2) continue;
                Identifier enchantID = Identifier.tryParse(enchantParsed[0]);
                Enchantment enchantment = Registry.ENCHANTMENT.get(enchantID);
                if(enchantment == null) continue;
                int level;
                try {
                    level = Integer.parseInt(enchantParsed[1]);
                } catch (NumberFormatException e) {
                    continue;
                }
                itemStack.addEnchantment(enchantment, level);
            }
            Configuration nbtConfig = itemConfig.getSection("NBT");
            if(nbtConfig != null) {
                NbtCompound itemStackNBT = itemStack.getOrCreateNbt();
                for(String nbtKey : nbtConfig.getKeys()) {
                    Object nbtValue = nbtConfig.get(nbtKey);
                    if(nbtValue instanceof Byte) {
                        itemStackNBT.putByte(nbtKey, (Byte) nbtValue);
                    } else if(nbtValue instanceof Integer) {
                        itemStackNBT.putInt(nbtKey, (Integer) nbtValue);
                    } else if(nbtValue instanceof Long) {
                        itemStackNBT.putLong(nbtKey, (Long) nbtValue);
                    } else if(nbtValue instanceof Float) {
                        itemStackNBT.putFloat(nbtKey, (Float) nbtValue);
                    } else if(nbtValue instanceof Double) {
                        itemStackNBT.putDouble(nbtKey, (Double) nbtValue);
                    } else if(nbtValue instanceof Boolean) {
                        itemStackNBT.putBoolean(nbtKey, (Boolean) nbtValue);
                    } else if(nbtValue instanceof String) {
                        itemStackNBT.putString(nbtKey, (String) nbtValue);
                    }
                }
                itemStack.setNbt(itemStackNBT);
            }
            return itemStack;
        } else return null;
    }

    public static void setLore(ItemStack item, List<String> lore) {
        NbtCompound displayNBT = item.getOrCreateSubNbt("display");
        if (lore != null && !lore.isEmpty()) {
            NbtList loreList = new NbtList();
            for(String line : lore) {
                loreList.add(NbtString.of(Text.Serializer.toJson(ColorUtil.parseColourToText(line))));
            }
            displayNBT.put("Lore", loreList);
        } else {
            displayNBT.remove("Lore");
            if(displayNBT.isEmpty()) {
                item.removeSubNbt("display");
            }
        }
    }
}
