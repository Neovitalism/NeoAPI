package me.neovitalism.neoapi.lang;

import me.neovitalism.neoapi.modloading.config.Configuration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class LangManager {
    private final Map<String, String> lang = new HashMap<>();

    public LangManager(Configuration langConfig) {
        if(langConfig != null) {
            for (String key : langConfig.getKeys()) {
                lang.put(key, langConfig.getString(key));
            }
        }
    }

    public @Nullable String getLang(String langKey) {
        return lang.get(langKey);
    }

    public @NotNull String getLangSafely(String langKey) {
        return lang.getOrDefault(langKey, "");
    }
}
