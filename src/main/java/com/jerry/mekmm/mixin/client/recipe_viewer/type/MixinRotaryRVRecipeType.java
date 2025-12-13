package com.jerry.mekmm.mixin.client.recipe_viewer.type;

import com.jerry.meklm.common.registries.LargeMachineBlocks;

import mekanism.client.recipe_viewer.type.RotaryRVRecipeType;
import mekanism.common.registries.MekanismBlocks;

import net.minecraft.world.level.ItemLike;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(value = RotaryRVRecipeType.class, remap = false)
public class MixinRotaryRVRecipeType {

    @ModifyArg(method = "<init>*", at = @At(value = "INVOKE", target = "Lmekanism/client/recipe_viewer/type/RotaryRVRecipeType;<init>(Lnet/minecraft/resources/ResourceLocation;Lmekanism/api/text/IHasTranslationKey;Ljava/util/List;)V"), index = 2)
    private static List<ItemLike> RotaryRVRecipeType(List<ItemLike> workstations) {
        return List.of(MekanismBlocks.ROTARY_CONDENSENTRATOR, LargeMachineBlocks.LARGE_ROTARY_CONDENSENTRATOR);
    }
}
