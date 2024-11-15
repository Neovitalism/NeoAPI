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
    private final Map<String, String> lang = new HashMap<>();

    public LangManager(Configuration langConfig) {
        if (langConfig == null) return;
        for (String key : langConfig.getKeys()) this.lang.put(key, langConfig.getString(key));
    }

    public @Nullable String getLang(String langKey) {
        return this.lang.get(langKey);
    }

    public @NotNull String getLangSafely(String langKey) {
        return this.lang.getOrDefault(langKey, "");
    }

    public void sendLang(Audience audience, String key, @Nullable Map<String, String> replacements) {
        String lang = this.getLang(key);
        if (lang == null) return;
        lang = StringUtil.replaceReplacements(lang, replacements);
        String prefix = this.getLang("Prefix");
        if (prefix != null) lang = prefix + lang;
        audience.sendMessage(ColorUtil.parseColour(lang));
    }
}
