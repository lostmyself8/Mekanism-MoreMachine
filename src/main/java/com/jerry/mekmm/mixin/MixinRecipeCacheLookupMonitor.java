package com.jerry.mekmm.mixin;

import com.jerry.mekmm.api.recipes.cache.PlantingCacheRecipe;

import mekanism.api.IContentsListener;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.ICachedRecipeHolder;
import mekanism.common.recipe.lookup.IRecipeLookupHandler;
import mekanism.common.recipe.lookup.IRecipeLookupHandler.ConstantUsageRecipeLookupHandler;
import mekanism.common.recipe.lookup.monitor.RecipeCacheLookupMonitor;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RecipeCacheLookupMonitor.class, remap = false)
public abstract class MixinRecipeCacheLookupMonitor<RECIPE extends MekanismRecipe> implements ICachedRecipeHolder<RECIPE>, IContentsListener {

    @Shadow
    @Final
    private IRecipeLookupHandler<RECIPE> handler;

    @Inject(method = "loadSavedData", at = @At(value = "INVOKE", target = "Lmekanism/api/recipes/cache/ICachedRecipeHolder;loadSavedData(Lmekanism/api/recipes/cache/CachedRecipe;I)V", shift = At.Shift.AFTER, by = 1))
    public void mixinLoadSavedData(@NotNull CachedRecipe<RECIPE> cached, int cacheIndex, CallbackInfo ci) {
        if (cached instanceof PlantingCacheRecipe c && handler instanceof ConstantUsageRecipeLookupHandler handler) {
            c.loadSavedUsageSoFar(handler.getSavedUsedSoFar(cacheIndex));
        }
    }
}
