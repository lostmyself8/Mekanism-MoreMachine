package com.jerry.mekmm.mixin.tile;

import com.jerry.mekaf.common.upgrade.PRCUpgradeData;

import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.providers.IBlockProvider;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = TileEntityPressurizedReactionChamber.class, remap = false)
public abstract class MixinTileEntityPressurizedReactionChamber extends TileEntityProgressMachine<PressurizedReactionRecipe> implements
                                                                ItemFluidChemicalRecipeLookupHandler<Gas, GasStack, PressurizedReactionRecipe> {

    @Shadow
    public BasicFluidTank inputFluidTank;
    @Shadow
    public IGasTank inputGasTank;
    @Shadow
    public IGasTank outputGasTank;
    @Shadow
    InputInventorySlot inputSlot;
    @Shadow
    OutputInventorySlot outputSlot;
    @Shadow
    EnergyInventorySlot energySlot;

    protected MixinTileEntityPressurizedReactionChamber(IBlockProvider blockProvider, BlockPos pos, BlockState state, List<CachedRecipe.OperationTracker.RecipeError> errorTypes, int baseTicksRequired) {
        super(blockProvider, pos, state, errorTypes, baseTicksRequired);
    }

    @Shadow
    public abstract PRCEnergyContainer getEnergyContainer();

    @Unique
    @Override
    public boolean isConfigurationDataCompatible(BlockEntityType<?> tileType) {
        return super.isConfigurationDataCompatible(tileType) || MoreMachineUtils.isSameAFTypeFactory(getBlockType(), tileType);
    }

    @Override
    public @Nullable PRCUpgradeData getUpgradeData() {
        return new PRCUpgradeData(redstone, getControlType(), getEnergyContainer(), getOperatingTicks(), energySlot,
                inputGasTank, inputFluidTank, inputSlot, outputSlot, outputGasTank, getComponents());
    }
}
