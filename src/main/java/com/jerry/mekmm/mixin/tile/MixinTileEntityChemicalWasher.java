package com.jerry.mekmm.mixin.tile;

import com.jerry.mekaf.common.upgrade.FluidSlurryToSlurryUpgradeData;

import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.chemical.slurry.ISlurryTank;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.FluidSlurryToSlurryRecipe;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = TileEntityChemicalWasher.class, remap = false)
public abstract class MixinTileEntityChemicalWasher extends TileEntityRecipeMachine<FluidSlurryToSlurryRecipe> implements
                                                    FluidChemicalRecipeLookupHandler<Slurry, SlurryStack, FluidSlurryToSlurryRecipe> {

    @Shadow
    public BasicFluidTank fluidTank;
    @Shadow
    public ISlurryTank inputTank;
    @Shadow
    public ISlurryTank outputTank;
    @Shadow
    FluidInventorySlot fluidSlot;
    @Shadow
    OutputInventorySlot fluidOutputSlot;
    @Shadow
    EnergyInventorySlot energySlot;

    protected MixinTileEntityChemicalWasher(IBlockProvider blockProvider, BlockPos pos, BlockState state, List<CachedRecipe.OperationTracker.RecipeError> errorTypes) {
        super(blockProvider, pos, state, errorTypes);
    }

    @Shadow
    public abstract MachineEnergyContainer<TileEntityChemicalWasher> getEnergyContainer();

    @Unique
    @Override
    public boolean isConfigurationDataCompatible(BlockEntityType<?> tileType) {
        return super.isConfigurationDataCompatible(tileType) || MoreMachineUtils.isSameAFTypeFactory(getBlockType(), tileType);
    }

    @Override
    public @Nullable FluidSlurryToSlurryUpgradeData getUpgradeData() {
        return new FluidSlurryToSlurryUpgradeData(redstone, getControlType(), getEnergyContainer(), 0,
                energySlot, fluidSlot, fluidOutputSlot, inputTank, fluidTank, outputTank, getComponents());
    }
}
