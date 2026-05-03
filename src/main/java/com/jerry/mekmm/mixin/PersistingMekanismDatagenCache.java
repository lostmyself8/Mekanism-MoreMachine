package com.jerry.mekmm.mixin;

import com.jerry.datagen.PersistingDisabledProvidersProvider;

import net.minecraft.WorldVersion;
import net.minecraft.data.DataGenerator.Cached;
import net.minecraft.data.HashCache;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.nio.file.Path;
import java.util.Collection;

@Mixin(Cached.class)
public abstract class PersistingMekanismDatagenCache {

    @WrapOperation(method = "run", at = @At(value = "NEW", target = "net/minecraft/data/HashCache"))
    public HashCache newHashCache(Path rootDir, Collection<String> providerIds, WorldVersion version, Operation<HashCache> original) {
        HashCache constructed = original.call(rootDir, providerIds, version);
        PersistingDisabledProvidersProvider.captureGlobalCache(constructed);
        return constructed;
    }
}
