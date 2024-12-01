package me.neovitalism.neoapi.helpers;

import me.neovitalism.neoapi.NeoAPI;
import me.neovitalism.neoapi.config.Configuration;
import me.neovitalism.neoapi.utils.ColorUtil;
import me.neovitalism.neoapi.utils.StringUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemHelper {
    public static Identifier getIdentifier(Item item) {
        return Registries.ITEM.getId(item);
    }

    public static ItemStack fromConfig(Configuration config, @Nullable Map<String, String> replacements) {
        ItemStack item = ItemHelper.createItemStack(StringUtil.replaceFromConfig(config,"material", replacements));
        ItemHelper.setDisplayName(item, StringUtil.replaceFromConfig(config, "name", replacements));
        List<String> lore = config.getStringList("lore");
        lore.replaceAll(input -> StringUtil.replaceReplacements(input, replacements));
        ItemHelper.setLore(item, lore);
        item.setCount(config.getInt("amount", 1));
        Configuration enchantSection = config.getSection("enchants");
        for (String key : enchantSection.getKeys()) {
            int level = enchantSection.getInt(key);
            ItemHelper.addEnchantment(item, key, level);
        }
        if (config.contains("custom-model-data"))
            ItemHelper.setCustomModelData(item, config.getInt("custom-model-data"));
        if (config.contains("max-stack-size"))
            ItemHelper.setMaxStackSize(item, config.getInt("max-stack-size"));
        ItemHelper.hideTooltip(item, config.getBoolean("hide-tooltip"));
        ItemHelper.hideAdditionalTooltip(item, config.getBoolean("hide-additional-tooltip"));
        ItemHelper.setUnbreakable(item, config.getBoolean("unbreakable"));
        ItemHelper.setFireResistant(item, config.getBoolean("fire-resistant"));
        ItemHelper.setEnchantGlint(item, config.getBoolean("enchant-glint"));
        Configuration customDataConfig = config.getSection("custom-data");
        if (customDataConfig == null) return item;
        for (String nbtKey : customDataConfig.getKeys()) {
            Object nbtValue = customDataConfig.get(nbtKey);
            if (nbtValue instanceof String) nbtValue = StringUtil.replaceObject((String) nbtValue, replacements);
            ItemHelper.setCustomData(item, nbtKey, nbtValue);
        }
        return item;
    }

    public static ItemStack createItemStack(String materialString) {
        if (materialString == null) return null;
        Item material = Registries.ITEM.get(Identifier.of(materialString));
        if (material == Items.AIR) return null;
        return new ItemStack(material);
    }

    public static void setDisplayName(ItemStack item, String name) {
        item.set(DataComponentTypes.ITEM_NAME, (name != null) ? ColorUtil.parseColourToText(name) : null);
    }

    public static void setLore(ItemStack item, List<String> lore) {
        List<Text> loreText = new ArrayList<>();
        if (lore != null) for (String line : lore) loreText.add(ColorUtil.parseColourToText(line));
        item.set(DataComponentTypes.LORE, (!loreText.isEmpty()) ? new LoreComponent(loreText) : null);
    }

    public static Enchantment getEnchant(String enchantID) {
        Registry<Enchantment> enchantRegistry = NeoAPI.getServer().getRegistryManager().get(RegistryKeys.ENCHANTMENT);
        return enchantRegistry.get(Identifier.of(enchantID));
    }

    public static void addEnchantment(ItemStack item, String enchantID, int level) {
        Enchantment enchant = ItemHelper.getEnchant(enchantID);
        if (enchant == null) return;
        ItemHelper.addEnchantment(item, enchant, level);
    }

    public static void addEnchantment(ItemStack item, Enchantment enchantment, int level) {
        Registry<Enchantment> enchantRegistry = NeoAPI.getServer().getRegistryManager().get(RegistryKeys.ENCHANTMENT);
        item.addEnchantment(enchantRegistry.getEntry(enchantment), level);
    }

    public static void setUnbreakable(ItemStack item, boolean value) {
        item.set(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(value));
    }

    public static void setCustomModelData(ItemStack item, int value) {
        item.set(DataComponentTypes.CUSTOM_MODEL_DATA, (value > 0) ? new CustomModelDataComponent(value) : null);
    }

    public static void setFireResistant(ItemStack item, boolean value) {
        item.set(DataComponentTypes.FIRE_RESISTANT, (value) ? Unit.INSTANCE : null);
    }

    public static void hideTooltip(ItemStack item, boolean value) {
        item.set(DataComponentTypes.HIDE_TOOLTIP, (value) ? Unit.INSTANCE : null);
    }

    public static void hideAdditionalTooltip(ItemStack item, boolean value) {
        item.set(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP, (value) ? Unit.INSTANCE : null);
    }

    public static void setMaxStackSize(ItemStack item, int amount) {
        item.set(DataComponentTypes.MAX_STACK_SIZE, Math.clamp(amount, 1, 99));
    }

    public static void setEnchantGlint(ItemStack item, boolean value) {
        item.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, value);
    }

    public static void setCustomData(ItemStack item, String key, Object value) {
        NbtComponent customData = item.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound customNBT = (customData != null) ? customData.copyNbt() : new NbtCompound();
        NbtElement element = ItemHelper.getNBTElement(value);
        if (element != null) customNBT.put(key, element);
        item.set(DataComponentTypes.CUSTOM_DATA, (!customNBT.isEmpty()) ? NbtComponent.of(customNBT) : null);
    }

    public static NbtElement getCustomData(ItemStack item, String key) {
        NbtComponent customData = item.get(DataComponentTypes.CUSTOM_DATA);
        if (customData == null) return null;
        return customData.copyNbt().get(key);
    }

    public static boolean hasCustomValue(ItemStack item, String key) {
        return ItemHelper.getCustomData(item, key) != null;
    }

    public static NbtElement getNBTElement(Object input) {
        if (input instanceof NbtCompound) return (NbtCompound) input;
        if (input instanceof Byte) return NbtByte.of((Byte) input);
        if (input instanceof Integer) return NbtInt.of((Integer) input);
        if (input instanceof Long) return NbtLong.of((Long) input);
        if (input instanceof Float) return NbtFloat.of((Float) input);
        if (input instanceof Double) return NbtDouble.of((Double) input);
        if (input instanceof Boolean) return NbtByte.of((Boolean) input);
        if (input instanceof String) return NbtString.of((String) input);
        if (input instanceof List<?>) {
            if (((List<?>) input).size() == 0) return null;
            Object listObject = ((List<?>) input).get(0);
            if (listObject instanceof Byte) {
                return new NbtByteArray((List<Byte>) input);
            } else if (listObject instanceof Integer) {
                return new NbtIntArray((List<Integer>) input);
            } else if (listObject instanceof Long) {
                return new NbtLongArray((List<Long>) input);
            } else {
                NbtList nbtList = new NbtList();
                for (var value : ((List<?>) input)) nbtList.add(ItemHelper.getNBTElement(value));
                return nbtList;
            }
        }
        return null;
    }

    public static void giveOrDropItem(ServerPlayerEntity player, ItemStack item) {
        if (player.giveItemStack(item)) return;
        ItemEntity itemEntity = new ItemEntity(player.getWorld(), player.getX(), player.getY(), player.getZ(), item);
        player.getWorld().spawnEntity(itemEntity);
    }
}
