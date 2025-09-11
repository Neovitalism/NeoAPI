package me.neovitalism.neoapi.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import me.neovitalism.neoapi.helpers.ItemHelper;
import me.neovitalism.neoapi.lang.LangManager;
import me.neovitalism.neoapi.objects.Location;
import me.neovitalism.neoapi.utils.TimeUtil;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

public final class Configuration {
    private static final char SEPARATOR = '.';
    final Map<String, Object> self;
    private Configuration defaults;

    public Configuration() {
        this(null);
    }

    public Configuration(Configuration defaults) {
        this(new LinkedHashMap<String, Object>(), defaults);
    }

    Configuration(Map<?, ?> map, Configuration defaults) {
        this.self = new LinkedHashMap<>();
        this.defaults = defaults;

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = (entry.getKey() == null) ? "null" : entry.getKey().toString();

            if (entry.getValue() instanceof Map) {
                this.self.put(key, new Configuration((Map<?, ?>) entry.getValue(), (defaults == null) ? null : defaults.getSection(key)));
            } else {
                this.self.put(key, entry.getValue());
            }
        }
    }

    public void setDefaults(Configuration def) {
        this.defaults = def;
    }

    private Configuration getSectionFor(String path) {
        int index = path.indexOf(SEPARATOR);
        if (index == -1) {
            return this;
        }

        String root = path.substring(0, index);
        Object section = self.get(root);
        if (section == null) {
            section = new Configuration((defaults == null) ? null : defaults.getSection(root));
            self.put(root, section);
        }

        return (Configuration) section;
    }

    private String getChild(String path) {
        int index = path.indexOf(SEPARATOR);
        return (index == -1) ? path : path.substring(index + 1);
    }

    public <T> T get(String path, T def) {
        Configuration section = getSectionFor(path);
        Object val;
        if (section == this) {
            val = self.get(path);
        } else {
            val = section.get(getChild(path), def);
        }

        if (val == null && def instanceof Configuration) {
            self.put(path, def);
        }

        return (val != null) ? (T) val : def;
    }

    public boolean contains(String path) {
        return get(path, null) != null;
    }

    public Object get(String path) {
        return get(path, getDefault(path));
    }

    public Object getDefault(String path) {
        return (defaults == null) ? null : defaults.get(path);
    }

    public void set(String path, Object value) {
        if (value instanceof Map) {
            value = new Configuration((Map<?, ?>) value, (defaults == null) ? null : defaults.getSection(path));
        }

        Configuration section = getSectionFor(path);
        if (section == this) {
            if (value == null) {
                self.remove(path);
            } else {
                self.put(path, value);
            }
        } else {
            section.set(getChild(path), value);
        }
    }

    public Configuration getSection(String path) {
        Object def = getDefault(path);
        return (Configuration) get(path, (def instanceof Configuration) ? def : new Configuration((defaults == null) ? null : defaults.getSection(path)));
    }

    public Collection<String> getKeys() {
        return new LinkedHashSet<>(self.keySet());
    }

    public byte getByte(String path) {
        Object def = getDefault(path);
        return getByte(path, (def instanceof Number) ? ((Number) def).byteValue() : 0);
    }

    public byte getByte(String path, byte def) {
        Number val = get(path, def);
        return (val != null) ? val.byteValue() : def;
    }

    public List<Byte> getByteList(String path) {
        List<?> list = getList(path);
        List<Byte> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).byteValue());
            }
        }

        return result;
    }

    public short getShort(String path) {
        Object def = getDefault(path);
        return getShort(path, (def instanceof Number) ? ((Number) def).shortValue() : 0);
    }

    public short getShort(String path, short def) {
        Number val = get(path, def);
        return (val != null) ? val.shortValue() : def;
    }

    public List<Short> getShortList(String path) {
        List<?> list = getList(path);
        List<Short> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).shortValue());
            }
        }

        return result;
    }

    public int getInt(String path) {
        Object def = getDefault(path);
        return getInt(path, (def instanceof Number) ? ((Number) def).intValue() : 0);
    }

    public int getInt(String path, int def) {
        Number val = get(path, def);
        return (val != null) ? val.intValue() : def;
    }

    public List<Integer> getIntList(String path) {
        List<?> list = getList(path);
        List<Integer> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).intValue());
            }
        }

        return result;
    }

    public long getLong(String path) {
        Object def = getDefault(path);
        return getLong(path, (def instanceof Number) ? ((Number) def).longValue() : 0);
    }

    public long getLong(String path, long def) {
        Number val = get(path, def);
        return (val != null) ? val.longValue() : def;
    }

    public List<Long> getLongList(String path) {
        List<?> list = getList(path);
        List<Long> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).longValue());
            }
        }

        return result;
    }

    public float getFloat(String path) {
        Object def = getDefault(path);
        return getFloat(path, (def instanceof Number) ? ((Number) def).floatValue() : 0);
    }

    public float getFloat(String path, float def) {
        Number val = get(path, def);
        return (val != null) ? val.floatValue() : def;
    }

    public List<Float> getFloatList(String path) {
        List<?> list = getList(path);
        List<Float> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).floatValue());
            }
        }

        return result;
    }

    public double getDouble(String path) {
        Object def = getDefault(path);
        return getDouble(path, (def instanceof Number) ? ((Number) def).doubleValue() : 0);
    }

    public double getDouble(String path, double def) {
        Number val = get(path, def);
        return (val != null) ? val.doubleValue() : def;
    }

    public List<Double> getDoubleList(String path) {
        List<?> list = getList(path);
        List<Double> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).doubleValue());
            }
        }

        return result;
    }

    public boolean getBoolean(String path) {
        Object def = getDefault(path);
        return getBoolean(path, (def instanceof Boolean) ? (Boolean) def : false);
    }

    public boolean getBoolean(String path, boolean def) {
        Boolean val = get(path, def);
        return (val != null) ? val : def;
    }

    public List<Boolean> getBooleanList(String path) {
        List<?> list = getList(path);
        List<Boolean> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Boolean) {
                result.add((Boolean) object);
            }
        }

        return result;
    }

    public char getChar(String path) {
        Object def = getDefault(path);
        return getChar(path, (def instanceof Character) ? (Character) def : '\u0000');
    }

    public char getChar(String path, char def) {
        Character val = get(path, def);
        return (val != null) ? val : def;
    }

    public List<Character> getCharList(String path) {
        List<?> list = getList(path);
        List<Character> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Character) {
                result.add((Character) object);
            }
        }

        return result;
    }

    public String getString(String path) {
        Object def = getDefault(path);
        return getString(path, (def instanceof String) ? (String) def : "");
    }

    public String getString(String path, String def) {
        String val = get(path, def);
        return (val != null) ? val : def;
    }

    public List<String> getStringList(String path) {
        List<?> list = getList(path);
        List<String> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof String) {
                result.add((String) object);
            }
        }

        return result;
    }

    public List<?> getList(String path) {
        Object def = getDefault(path);
        return getList(path, (def instanceof List<?>) ? (List<?>) def : Collections.EMPTY_LIST);
    }

    public List<?> getList(String path, List<?> def) {
        List<?> val = get(path, def);
        return (val != null) ? val : def;
    }

    public UUID getUUID(String path) {
        return UUID.fromString(this.getString(path));
    }

    public Location getLocation(String path) {
        return this.getLocation(path, null);
    }

    public Location getLocation(String path, Location def) {
        Configuration section = this.getSection(path);
        if (section == null) return def;
        return new Location(section);
    }

    public ItemStack getItemStack(String path) {
        return this.getItemStack(path, null, null);
    }

    public ItemStack getItemStack(String path, ItemStack def) {
        return this.getItemStack(path, null, def);
    }

    public ItemStack getItemStack(String path, Map<String, String> replacements) {
        return this.getItemStack(path, replacements, null);
    }

    public ItemStack getItemStack(String path, Map<String, String> replacements, ItemStack def) {
        Configuration section = this.getSection(path);
        if (section == null) return def;
        return ItemHelper.fromConfig(section, replacements);
    }

    public long getSeconds(String path, String def) {
        return TimeUtil.parseSeconds(this.getString(path, def));
    }

    public LangManager getLangManager(String path) {
        return new LangManager(this.getSection(path));
    }

    public LangManager getLangManager(String path, boolean capitalized) {
        return new LangManager(this.getSection(path), capitalized);
    }

    public <T> Map<String, T> getMap(String mapKey, Function<Configuration, T> mappingFunction) {
        Map<String, T> map = new HashMap<>();
        Configuration config = this.getSection(mapKey);
        if (config == null) return map;
        for (String key : config.getKeys()) {
            Configuration section = config.getSection(key);
            if (section == null) continue;
            map.put(key, mappingFunction.apply(section));
        }
        return map;
    }

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Configuration.class, (JsonSerializer<Configuration>) (src, type, ctx) -> {
                JsonObject obj = new JsonObject();
                for (Map.Entry<String, Object> entry : src.self.entrySet()) {
                    if (entry.getValue() instanceof Configuration) {
                        obj.add(entry.getKey(), ctx.serialize(entry.getValue(), Configuration.class));
                    } else {
                        obj.add(entry.getKey(), ctx.serialize(entry.getValue()));
                    }
                }
                return obj;
            }).disableHtmlEscaping().create();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() {}.getType();

    public String toJson() {
        return Configuration.GSON.toJson(this.self);
    }

    public static Configuration fromJson(String json) {
        Map<String, Object> map = GSON.fromJson(json, MAP_TYPE);
        return new Configuration(map, null);
    }
}
