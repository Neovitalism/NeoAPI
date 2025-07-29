package me.neovitalism.neoapi.lang;

import me.neovitalism.neoapi.config.Configuration;
import me.neovitalism.neoapi.utils.ColorUtil;
import me.neovitalism.neoapi.utils.StringUtil;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class LangManager {
    public static final LangManager EMPTY = new LangManager();

    private final boolean capitalized;
    private final Map<String, String> lang = new HashMap<>();

    private LangManager() {
        this.capitalized = true;
    }

    public LangManager(Configuration langConfig) {
        this(langConfig, true);
    }

    public LangManager(Configuration langConfig, boolean capitalized) {
        this.capitalized = capitalized;
        if (langConfig == null) return;
        this.addLang(null, langConfig);
    }

    private void addLang(String prefix, Configuration langConfig) {
        for (String key : langConfig.getKeys()) {
            String langKey = ((prefix == null) ? "" : prefix + ".") + key;
            Object obj = langConfig.get(langKey);
            if (obj instanceof Configuration) {
                this.addLang(langKey, langConfig.getSection(key));
            } else if (obj instanceof String) {
                this.lang.put(langKey, langConfig.getString(key));
            }
        }
    }

    public @Nullable String getLang(String langKey) {
        return this.lang.get(langKey);
    }

    public @NotNull String getLangSafely(String langKey) {
        return this.lang.getOrDefault(langKey, "");
    }

    public @NotNull String getOrDefault(String langKey, String def) {
        return this.lang.getOrDefault(langKey, def);
    }

    public void sendLang(Audience audience, String key, @Nullable Map<String, String> replacements) {
        String lang = this.getLang(key);
        if (lang == null || lang.isBlank()) return;
        lang = StringUtil.replaceReplacements(lang, replacements);
        String prefix = this.getLang((this.capitalized) ? "Prefix" : "prefix");
        if (prefix != null) lang = prefix + lang;
        audience.sendMessage(ColorUtil.parseColour(lang));
    }
}
