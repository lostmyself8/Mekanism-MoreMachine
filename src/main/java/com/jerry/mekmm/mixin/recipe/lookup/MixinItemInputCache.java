package com.jerry.mekmm.mixin.recipe.lookup;

import mekanism.common.recipe.lookup.cache.type.ItemInputCache;

import net.minecraft.world.item.crafting.Ingredient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ItemInputCache.class, remap = false)
public abstract class MixinItemInputCache {

    // NeoForge 26.2 custom ingredients can be simple while still rejecting getValues().
    // Keep compound/component special handling and use the complex matcher for other custom ingredients.
    @Redirect(method = "mapIngredient", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Ingredient;isSimple()Z"))
    private boolean mekmm$useVanillaValuesOnly(Ingredient ingredient) {
        return !ingredient.isCustom() && ingredient.isSimple();
    }
}
