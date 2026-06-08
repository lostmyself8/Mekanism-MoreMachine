package com.jerry.mekmm.mixin.tile;

import com.jerry.mekaf.common.upgrade.FluidChemicalToChemicalUpgradeData;

import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.chemical.IChemicalTank;
import mekanism.api.recipes.FluidChemicalToChemicalRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.FluidInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.recipe.lookup.IDoubleRecipeLookupHandler.FluidChemicalRecipeLookupHandler;
import mekanism.common.tile.machine.TileEntityChemicalWasher;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;

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

@Mixin(value = TileEntityChemicalWasher.class, remap = false)
public abstract class MixinTileEntityChemicalWasher extends TileEntityRecipeMachine<FluidChemicalToChemicalRecipe> implements
                                                    FluidChemicalRecipeLookupHandler<FluidChemicalToChemicalRecipe> {

    @Shadow
    private MachineEnergyContainer<TileEntityChemicalWasher> energyContainer;
    @Shadow
    public BasicFluidTank fluidTank;
    @Shadow
    public IChemicalTank inputTank;
    @Shadow
    public IChemicalTank outputTank;
    @Shadow
    FluidInventorySlot fluidSlot;
    @Shadow
    OutputInventorySlot fluidOutputSlot;
    @Shadow
    EnergyInventorySlot energySlot;

    protected MixinTileEntityChemicalWasher(Holder<Block> blockProvider, BlockPos pos, BlockState state, List<CachedRecipe.OperationTracker.RecipeError> errorTypes) {
        super(blockProvider, pos, state, errorTypes);
    }

    @Unique
    @Override
    public boolean isConfigurationDataCompatible(Block type) {
        return super.isConfigurationDataCompatible(type) || MoreMachineUtils.isSameAFTypeFactory(getBlockHolder(), type);
    }

    @Override
    public @Nullable FluidChemicalToChemicalUpgradeData getUpgradeData(Provider provider) {
        return new FluidChemicalToChemicalUpgradeData(provider, redstone, getControlType(), energyContainer, 0, 0,
                energySlot, fluidSlot, fluidOutputSlot, inputTank, fluidTank, outputTank, getComponents(), problemPath());
    }
}
