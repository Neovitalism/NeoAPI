package me.neovitalism.neoapi.utils;

import me.neovitalism.neoapi.objects.interfaces.NeoEntity;
import net.minecraft.entity.Entity;

public class EntityUtil {
    public static void setOverrideTeamColor(Entity entity, String hex) {
        ((NeoEntity) entity).setOverrideColor(Integer.parseInt(hex.replace("#", ""), 16));
    }
}
