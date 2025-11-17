package com.jerry.datagen.common.recipe.builder;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.common.registries.MekanismRecipeSerializers;

import net.minecraft.world.level.ItemLike;

@NothingNullByDefault
public class MoreMachineDataShapedRecipeBuilder extends ExtendedShapedRecipeBuilder {

    private MoreMachineDataShapedRecipeBuilder(ItemLike result, int count) {
        super(MekanismRecipeSerializers.MEK_DATA.get(), result, count);
    }

    public static MoreMachineDataShapedRecipeBuilder shapedRecipe(ItemLike result) {
        return shapedRecipe(result, 1);
    }

    public static MoreMachineDataShapedRecipeBuilder shapedRecipe(ItemLike result, int count) {
        return new MoreMachineDataShapedRecipeBuilder(result, count);
    }
}
