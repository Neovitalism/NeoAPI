package me.neovitalism.neoapi.helpers;

import me.neovitalism.neoapi.modloading.config.Configuration;
import me.neovitalism.neoapi.utils.ColorUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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
            if(name != null) itemStack.setCustomName(ColorUtil.parseItemName(name));
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
                for(String nbtKey : nbtConfig.getKeys()) {
                    addNBT(itemStack, nbtKey, nbtConfig.get(nbtKey));
                }
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

    public static void addNBT(ItemStack itemStack, String nbtKey, Object nbtValue) {
        NbtCompound itemStackNBT = itemStack.getOrCreateNbt();
        NbtElement element = getNBTElement(nbtValue);
        if(element != null) {
            itemStackNBT.put(nbtKey, element);
        }
    }

    public static NbtElement getNBTElement(Object input) {
        if(input instanceof NbtCompound) return (NbtCompound) input;
        if(input instanceof Byte) return NbtByte.of((Byte) input);
        if(input instanceof Integer) return NbtInt.of((Integer) input);
        if(input instanceof Long) return NbtLong.of((Long) input);
        if(input instanceof Float) return NbtFloat.of((Float) input);
        if(input instanceof Double) return NbtDouble.of((Double) input);
        if(input instanceof Boolean) return NbtByte.of((Boolean) input);
        if(input instanceof String) return NbtString.of((String) input);
        if(input instanceof List<?>) {
            if(((List<?>) input).size() > 0) {
                Object listObject = ((List<?>) input).get(0);
                if(listObject instanceof Byte) {
                    return new NbtByteArray((List<Byte>) input);
                } else if(listObject instanceof Integer) {
                    return new NbtIntArray((List<Integer>) input);
                } else if(listObject instanceof Long) {
                    return new NbtLongArray((List<Long>) input);
                } else {
                    NbtList nbtList = new NbtList();
                    for(var value : ((List<?>) input)) {
                        nbtList.add(getNBTElement(value));
                    }
                    return nbtList;
                }
            }
        }
        return null;
    }
}
