package com.jerry.mekmm.mixin.tile;

import com.jerry.mekaf.common.upgrade.PRCUpgradeData;

import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.chemical.IChemicalTank;
import mekanism.api.recipes.PressurizedReactionRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.common.capabilities.energy.PRCEnergyContainer;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.recipe.lookup.ITripleRecipeLookupHandler.ItemFluidChemicalRecipeLookupHandler;
import mekanism.common.tile.machine.TileEntityPressurizedReactionChamber;
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

@Mixin(value = TileEntityPressurizedReactionChamber.class, remap = false)
public abstract class MixinTileEntityPressurizedReactionChamber extends TileEntityProgressMachine<PressurizedReactionRecipe> implements
                                                                ItemFluidChemicalRecipeLookupHandler<PressurizedReactionRecipe> {

    @Shadow
    public BasicFluidTank inputFluidTank;
    @Shadow
    public IChemicalTank inputGasTank;
    @Shadow
    public IChemicalTank outputGasTank;
    @Shadow
    InputInventorySlot inputSlot;
    @Shadow
    OutputInventorySlot outputSlot;
    @Shadow
    EnergyInventorySlot energySlot;

    protected MixinTileEntityPressurizedReactionChamber(Holder<Block> blockProvider, BlockPos pos, BlockState state, List<CachedRecipe.OperationTracker.RecipeError> errorTypes, int baseTicksRequired) {
        super(blockProvider, pos, state, errorTypes, baseTicksRequired);
    }

    @Shadow
    public abstract PRCEnergyContainer getEnergyContainer();

    @Unique
    @Override
    public boolean isConfigurationDataCompatible(Block type) {
        return super.isConfigurationDataCompatible(type) || MoreMachineUtils.isSameAFTypeFactory(getBlockHolder(), type);
    }

    @Override
    public @Nullable PRCUpgradeData getUpgradeData(Provider provider) {
        return new PRCUpgradeData(provider, redstone, getControlType(), getEnergyContainer(), getOperatingTicks(), energySlot,
                inputGasTank, inputFluidTank, inputSlot, outputSlot, outputGasTank, getComponents());
    }
}
