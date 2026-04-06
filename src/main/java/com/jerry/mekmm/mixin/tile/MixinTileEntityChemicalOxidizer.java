package com.jerry.mekmm.mixin.tile;

import com.jerry.mekaf.common.upgrade.ItemToGasUpgradeData;

import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.ItemStackToGasRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.recipe.lookup.ISingleRecipeLookupHandler.ItemRecipeLookupHandler;
import mekanism.common.tile.machine.TileEntityChemicalOxidizer;
import mekanism.common.tile.prefab.TileEntityProgressMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = TileEntityChemicalOxidizer.class, remap = false)
public abstract class MixinTileEntityChemicalOxidizer extends TileEntityProgressMachine<ItemStackToGasRecipe> implements ItemRecipeLookupHandler<ItemStackToGasRecipe> {

    @Shadow
    public IGasTank gasTank;
    @Shadow
    InputInventorySlot inputSlot;
    @Shadow
    EnergyInventorySlot energySlot;

    protected MixinTileEntityChemicalOxidizer(IBlockProvider blockProvider, BlockPos pos, BlockState state, List<CachedRecipe.OperationTracker.RecipeError> errorTypes, int baseTicksRequired) {
        super(blockProvider, pos, state, errorTypes, baseTicksRequired);
    }

    @Shadow
    public abstract MachineEnergyContainer<TileEntityChemicalOxidizer> getEnergyContainer();

    @Unique
    @Override
    public boolean isConfigurationDataCompatible(BlockEntityType<?> tileType) {
        return super.isConfigurationDataCompatible(tileType) || MoreMachineUtils.isSameAFTypeFactory(getBlockType(), tileType);
    }

    @Override
    public @Nullable ItemToGasUpgradeData getUpgradeData() {
        return new ItemToGasUpgradeData(redstone, getControlType(), getEnergyContainer(),
                getOperatingTicks(), energySlot, inputSlot, gasTank, getComponents());
    }
}
