package com.jerry.mekmm.mixin.tile;

import com.jerry.mekaf.common.upgrade.ItemGasToMergedUpgradeData;

import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.ChemicalDissolutionRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.chemical.GasInventorySlot;
import mekanism.common.recipe.lookup.IDoubleRecipeLookupHandler.ItemChemicalRecipeLookupHandler;
import mekanism.common.tile.machine.TileEntityChemicalDissolutionChamber;
import mekanism.common.tile.prefab.TileEntityProgressMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = TileEntityChemicalDissolutionChamber.class, remap = false)
public abstract class MixinTileEntityChemicalDissolutionChamber extends TileEntityProgressMachine<ChemicalDissolutionRecipe> implements
                                                                ItemChemicalRecipeLookupHandler<Gas, GasStack, ChemicalDissolutionRecipe> {

    @Shadow
    public IGasTank injectTank;
    @Shadow
    public MergedChemicalTank outputTank;
    @Shadow
    GasInventorySlot gasInputSlot;
    @Shadow
    InputInventorySlot inputSlot;
    @Shadow
    EnergyInventorySlot energySlot;

    protected MixinTileEntityChemicalDissolutionChamber(IBlockProvider blockProvider, BlockPos pos, BlockState state, List<CachedRecipe.OperationTracker.RecipeError> errorTypes, int baseTicksRequired) {
        super(blockProvider, pos, state, errorTypes, baseTicksRequired);
    }

    @Shadow
    public abstract MachineEnergyContainer<TileEntityChemicalDissolutionChamber> getEnergyContainer();

    @Unique
    @Override
    public boolean isConfigurationDataCompatible(BlockEntityType<?> tileType) {
        return super.isConfigurationDataCompatible(tileType) || MoreMachineUtils.isSameAFTypeFactory(getBlockType(), tileType);
    }

    @Override
    public @Nullable ItemGasToMergedUpgradeData getUpgradeData() {
        return new ItemGasToMergedUpgradeData(redstone, getControlType(), getEnergyContainer(),
                getOperatingTicks(), energySlot, gasInputSlot, inputSlot, injectTank, outputTank, getComponents());
    }
}
