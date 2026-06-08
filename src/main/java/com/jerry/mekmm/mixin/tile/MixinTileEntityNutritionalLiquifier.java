package com.jerry.mekmm.mixin.tile;

import com.jerry.mekaf.common.upgrade.NutritionLiquifyingUpgradeData;

import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.fluid.IFluidTank;
import mekanism.api.recipes.basic.BasicItemStackToFluidOptionalItemRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.tile.machine.TileEntityNutritionalLiquifier;
import mekanism.common.tile.prefab.TileEntityProgressMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = TileEntityNutritionalLiquifier.class, remap = false)
public abstract class MixinTileEntityNutritionalLiquifier extends TileEntityProgressMachine<BasicItemStackToFluidOptionalItemRecipe> {

    @Shadow
    private MachineEnergyContainer<TileEntityNutritionalLiquifier> energyContainer;
    @Shadow
    public IFluidTank fluidTank;
    @Shadow
    InputInventorySlot inputSlot;
    @Shadow
    OutputInventorySlot outputSlot;
    @Shadow
    EnergyInventorySlot energySlot;

    protected MixinTileEntityNutritionalLiquifier(Holder<Block> blockProvider, BlockPos pos, BlockState state, List<CachedRecipe.OperationTracker.RecipeError> errorTypes, int baseTicksRequired) {
        super(blockProvider, pos, state, errorTypes, baseTicksRequired);
    }

    @Unique
    @Override
    public boolean isConfigurationDataCompatible(Block type) {
        return super.isConfigurationDataCompatible(type) || MoreMachineUtils.isSameAFTypeFactory(getBlockHolder(), type);
    }

    @Override
    public @Nullable NutritionLiquifyingUpgradeData getUpgradeData(Provider provider) {
        return new NutritionLiquifyingUpgradeData(provider, redstone, getControlType(), energyContainer, getOperatingTicks(),
                energySlot, inputSlot, outputSlot, fluidTank, getComponents(), problemPath());
    }
}
