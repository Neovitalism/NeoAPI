package me.neovitalism.neoapi.mixins;

import me.neovitalism.neoapi.objects.interfaces.NeoEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Entity.class)
public abstract class EntityMixin implements NeoEntity {
    @Unique private Integer overrideTeamColor = null;

    @Inject(method = "getTeamColorValue", at = @At(value = "HEAD"), cancellable = true)
    public void neoAPI$getTeamColorValue(CallbackInfoReturnable<Integer> cir) {
        if (this.overrideTeamColor != null) cir.setReturnValue(this.overrideTeamColor);
    }

    @Unique
    @Override
    public void setOverrideColor(int color) {
        this.overrideTeamColor = color;
    }
}
