package me.neovitalism.neoapi.mixins;

import me.neovitalism.neoapi.entity.EntityDataStorage;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Entity.class)
public abstract class EntityDataSaverMixin implements EntityDataStorage {
    private NbtCompound persistentData;

    @Override
    public NbtCompound getPersistentData() {
        if(persistentData == null) persistentData = new NbtCompound();
        return persistentData;
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    protected void neoAPI$addDataToNBT(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        if(persistentData != null) nbt.put("neoapi.persistent_data", persistentData);
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    protected void neoAPI$readDataFromNBT(NbtCompound nbt, CallbackInfo ci) {
        if(nbt.contains("neoapi.persistent_data", 10)) persistentData = nbt.getCompound("neoapi.persistent_data");
    }
}
