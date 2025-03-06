package me.neovitalism.neoapi.storage;

public enum StorageType {
    YAML,
    MARIADB;

    public static StorageType getByName(String id) {
        StorageType type = null;
        for (StorageType storageType : StorageType.values()) {
            if (storageType.name().equalsIgnoreCase(id)) type = storageType;
        }
        return (type != null) ? type : YAML;
    }
}
