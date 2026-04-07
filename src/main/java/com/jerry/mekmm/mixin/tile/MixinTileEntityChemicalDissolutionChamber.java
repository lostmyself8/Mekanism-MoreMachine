package com.jerry.mekmm.mixin.tile;

import com.jerry.mekaf.common.upgrade.ItemChemicalToChemicalUpgradeData;

import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.chemical.IChemicalTank;
import mekanism.api.recipes.ChemicalDissolutionRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.chemical.ChemicalInventorySlot;
import mekanism.common.recipe.lookup.IDoubleRecipeLookupHandler.ItemChemicalRecipeLookupHandler;
import mekanism.common.recipe.lookup.IRecipeLookupHandler.ConstantUsageRecipeLookupHandler;
import mekanism.common.tile.interfaces.IHasDumpButton;
import mekanism.common.tile.machine.TileEntityChemicalDissolutionChamber;
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

@Mixin(value = TileEntityChemicalDissolutionChamber.class, remap = false)
public abstract class MixinTileEntityChemicalDissolutionChamber extends TileEntityProgressMachine<ChemicalDissolutionRecipe> implements IHasDumpButton, ConstantUsageRecipeLookupHandler,
                                                                ItemChemicalRecipeLookupHandler<ChemicalDissolutionRecipe> {

    @Shadow
    private long usedSoFar;
    @Shadow
    public IChemicalTank injectTank;
    @Shadow
    public IChemicalTank outputTank;
    @Shadow
    ChemicalInventorySlot gasInputSlot;
    @Shadow
    InputInventorySlot inputSlot;
    @Shadow
    EnergyInventorySlot energySlot;

    protected MixinTileEntityChemicalDissolutionChamber(Holder<Block> blockProvider, BlockPos pos, BlockState state, List<CachedRecipe.OperationTracker.RecipeError> errorTypes, int baseTicksRequired) {
        super(blockProvider, pos, state, errorTypes, baseTicksRequired);
    }

    @Shadow
    public abstract MachineEnergyContainer<TileEntityChemicalDissolutionChamber> getEnergyContainer();

    @Unique
    @Override
    public boolean isConfigurationDataCompatible(Block type) {
        return super.isConfigurationDataCompatible(type) || MoreMachineUtils.isSameAFTypeFactory(getBlockHolder(), type);
    }

    @Override
    public @Nullable ItemChemicalToChemicalUpgradeData getUpgradeData(Provider provider) {
        return new ItemChemicalToChemicalUpgradeData(provider, redstone, getControlType(), getEnergyContainer(),
                getOperatingTicks(), usedSoFar, energySlot, gasInputSlot, inputSlot, injectTank, outputTank, getComponents());
    }
}
