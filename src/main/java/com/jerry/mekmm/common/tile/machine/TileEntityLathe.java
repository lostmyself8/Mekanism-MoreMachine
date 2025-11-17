package com.jerry.mekmm.common.tile.machine;

import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.tile.prefab.TileEntityElectricMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

public class TileEntityLathe extends TileEntityElectricMachine {

    public TileEntityLathe(BlockPos pos, BlockState state) {
        super(MoreMachineBlocks.CNC_LATHE, pos, state, 200);
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<ItemStackToItemStackRecipe, InputRecipeCache.SingleItem<ItemStackToItemStackRecipe>> getRecipeType() {
        return MoreMachineRecipeType.LATHING;
    }
}
