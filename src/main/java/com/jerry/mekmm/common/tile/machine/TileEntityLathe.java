package com.jerry.mekmm.common.tile.machine;

import com.jerry.mekmm.client.recipe_viewer.MMRecipeViewerRecipeType;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;
import com.jerry.mekmm.common.registries.MMBlocks;
import com.jerry.mekmm.common.util.MMUtils;
import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.client.recipe_viewer.type.IRecipeViewerRecipeType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.tile.prefab.TileEntityElectricMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileEntityLathe extends TileEntityElectricMachine {

    public TileEntityLathe(BlockPos pos, BlockState state) {
        super(MMBlocks.CNC_LATHE, pos, state, BASE_TICKS_REQUIRED);
    }

    @Override
    public @NotNull IMekanismRecipeTypeProvider<?, ItemStackToItemStackRecipe, InputRecipeCache.SingleItem<ItemStackToItemStackRecipe>> getRecipeType() {
        return MoreMachineRecipeType.LATHING;
    }

    @Override
    public @Nullable IRecipeViewerRecipeType<ItemStackToItemStackRecipe> recipeViewerType() {
        return MMRecipeViewerRecipeType.LATHE;
    }

    @Override
    public boolean isConfigurationDataCompatible(Block type) {
        return super.isConfigurationDataCompatible(type) || MMUtils.isSameMMTypeFactory(getBlockHolder(), type);
    }
}
