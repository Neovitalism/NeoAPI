package me.neovitalism.neoapi.objects;

import me.neovitalism.neoapi.config.Configuration;
import me.neovitalism.neoapi.storage.MariaDBConnection;
import me.neovitalism.neoapi.utils.LocationUtil;
import net.minecraft.server.world.ServerWorld;

import java.util.*;
import java.util.function.Function;

public class LocationMap<T> {
    private final String objectKey;
    private final Map<String, Map<Integer, Map<Integer, Map<Integer, T>>>> backingMap = new HashMap<>();

    private Function<T, Configuration> toConfigFunction = null;
    private Function<Configuration, T> fromConfigFunction = null;

    public LocationMap(String objectKey) {
        this.objectKey = objectKey;
    }

    public void setToConfigFunction(Function<T, Configuration> toConfigFunction) {
        this.toConfigFunction = toConfigFunction;
    }

    public void setFromConfigFunction(Function<Configuration, T> fromConfigFunction) {
        this.fromConfigFunction = fromConfigFunction;
    }

    public void put(Location loc, T obj) {
        this.backingMap.computeIfAbsent(loc.getWorldName(), __ -> new HashMap<>())
                .computeIfAbsent((int) loc.getX(), __ -> new HashMap<>())
                .computeIfAbsent((int) loc.getZ(), __ -> new HashMap<>())
                .put((int) loc.getY(), obj);
    }

    public T get(Location loc) {
        Map<Integer, Map<Integer, Map<Integer, T>>> worldMap = this.backingMap.get(loc.getWorldName());
        if (worldMap == null) return null;
        Map<Integer, Map<Integer, T>> xMap = worldMap.get((int) loc.getX());
        if (xMap == null) return null;
        Map<Integer, T> zMap = xMap.get((int) loc.getZ());
        if (zMap == null) return null;
        return zMap.get((int) loc.getY());
    }

    public List<T> values() {
        List<T> values = new ArrayList<>();
        for (Map.Entry<String, Map<Integer, Map<Integer, Map<Integer, T>>>> worldEntry : this.backingMap.entrySet()) {
            for (Map.Entry<Integer, Map<Integer, Map<Integer, T>>> xEntry : worldEntry.getValue().entrySet()) {
                for (Map.Entry<Integer, Map<Integer, T>> zEntry : xEntry.getValue().entrySet()) {
                    for (Map.Entry<Integer, T> yEntry : zEntry.getValue().entrySet()) values.add(yEntry.getValue());
                }
            }
        }
        return values;
    }

    public T remove(Location loc) {
        String worldName = loc.getWorldName();
        Map<Integer, Map<Integer, Map<Integer, T>>> worldMap = this.backingMap.get(worldName);
        if (worldMap == null) return null;
        Map<Integer, Map<Integer, T>> xMap = worldMap.get((int) loc.getX());
        if (xMap == null) return null;
        Map<Integer, T> zMap = xMap.get((int) loc.getZ());
        if (zMap == null) return null;
        T obj = zMap.remove((int) loc.getY());
        if (obj == null) return null;
        if (zMap.isEmpty()) {
            xMap.remove((int) loc.getZ());
            if (xMap.isEmpty()) {
                worldMap.remove((int) loc.getX());
                if (worldMap.isEmpty()) this.backingMap.remove(worldName);
            }
        }
        return obj;
    }

    public void load(Configuration config) {
        List<Configuration> objectConfigs = config.getList(this.objectKey, s -> s);
        for (Configuration objectConfig : objectConfigs) {
            T obj = this.fromConfigFunction.apply(objectConfig);
            Location location = objectConfig.getLocation("location");
            this.put(location, obj);
        }
    }

    public Configuration toConfig() {
        Configuration config = new Configuration(), objectConfigs = new Configuration();
        for (Map.Entry<String, Map<Integer, Map<Integer, Map<Integer, T>>>> worldEntry : this.backingMap.entrySet()) {
            for (Map.Entry<Integer, Map<Integer, Map<Integer, T>>> xEntry : worldEntry.getValue().entrySet()) {
                for (Map.Entry<Integer, Map<Integer, T>> zEntry : xEntry.getValue().entrySet()) {
                    for (Map.Entry<Integer, T> yEntry : zEntry.getValue().entrySet()) {
                        Configuration section = this.toConfigFunction.apply(yEntry.getValue());
                        Configuration location = new Configuration();
                        location.set("world", worldEntry.getKey());
                        location.set("x", xEntry.getKey());
                        location.set("y", yEntry.getKey());
                        location.set("z", zEntry.getKey());
                        section.set("location", location);
                        objectConfigs.set(UUID.randomUUID().toString(), section);
                    }
                }
            }
        }
        config.set(this.objectKey, objectConfigs);
        return config;
    }

    public String getTableArguments() {
        return "world VARCHAR(50) NOT NULL, x INT NOT NULL, y INT NOT NULL, z INT NOT NULL, " + this.objectKey + " JSON NOT NULL, PRIMARY KEY (world, x, y, z)";
    }

    private String getDBGetter(String databaseName) {
        return "SELECT data FROM " + databaseName + " WHERE world = ? AND x = ? AND y = ? AND z = ?";
    }

    private String getDBAdder(String databaseName) {
        return "INSERT INTO " + databaseName + " (world, x, y, z, " + this.objectKey + ") VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " + this.objectKey + " = VALUES(" + this.objectKey + ")";
    }

    private String getDBDeleter(String databaseName) {
        return "DELETE FROM " + databaseName + " WHERE world = ? AND x = ? AND y = ? AND z = ?";
    }

    public T getFromDatabase(MariaDBConnection conn, String dbName, Location location) {
        return conn.query(this.getDBGetter(dbName), statement -> {
            statement.setString(1, location.getWorldName());
            statement.setInt(2, (int) location.getX());
            statement.setInt(3, (int) location.getY());
            statement.setInt(4, (int) location.getZ());
        }, result -> {
            if (!result.next()) return null;
            Configuration config = Configuration.fromJson(result.getString(this.objectKey));
            return this.fromConfigFunction.apply(config);
        });
    }

    public void addToDatabase(MariaDBConnection conn, String dbName, Location location, T obj) {
        conn.query(this.getDBAdder(dbName), statement -> {
            statement.setString(1, location.getWorldName());
            statement.setInt(2, (int) location.getX());
            statement.setInt(3, (int) location.getY());
            statement.setInt(4, (int) location.getZ());
            statement.setString(5, this.toConfigFunction.apply(obj).toJson());
        });
    }

    public void removeFromDatabase(MariaDBConnection conn, String dbName, Location location) {
        conn.query(this.getDBDeleter(dbName), statement -> {
            statement.setString(1, location.getWorldName());
            statement.setInt(2, (int) location.getX());
            statement.setInt(3, (int) location.getY());
            statement.setInt(4, (int) location.getZ());
        });
    }

    public void populateFromDB(MariaDBConnection conn, String dbName) {
        conn.query("SELECT world, x, y, z, " + this.objectKey + " FROM " + dbName, statement -> {},
                result -> {
                    while (result.next()) {
                        ServerWorld world = LocationUtil.getWorld(result.getString("world"));
                        int x = result.getInt("x"), y = result.getInt("y"), z = result.getInt("z");
                        Location location = new Location(world, x, y, z);
                        Configuration config = Configuration.fromJson(result.getString(this.objectKey));
                        this.put(location, this.fromConfigFunction.apply(config));
                    }
                    return null;
                }
        );
    }

    public void sendAllToDB(MariaDBConnection conn, String dbName) {
        conn.queryBatch(this.getDBAdder(dbName), statement -> {
            for (Map.Entry<String, Map<Integer, Map<Integer, Map<Integer, T>>>> worldEntry : this.backingMap.entrySet()) {
                for (Map.Entry<Integer, Map<Integer, Map<Integer, T>>> xEntry : worldEntry.getValue().entrySet()) {
                    for (Map.Entry<Integer, Map<Integer, T>> zEntry : xEntry.getValue().entrySet()) {
                        for (Map.Entry<Integer, T> yEntry : zEntry.getValue().entrySet()) {
                            statement.setString(1, worldEntry.getKey());
                            statement.setInt(2, xEntry.getKey());
                            statement.setInt(3, yEntry.getKey());
                            statement.setInt(4, zEntry.getKey());
                            statement.setString(5, this.toConfigFunction.apply(yEntry.getValue()).toJson());
                            statement.addBatch();
                        }
                    }
                }
            }
        });
    }
}
