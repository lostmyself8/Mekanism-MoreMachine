package com.jerry.mekmm.mixin.tile;

import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.chemical.IChemicalTank;
import mekanism.api.recipes.ItemStackChemicalToItemStackRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.inventory.slot.ChemicalInventorySlot;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.recipe.lookup.IDoubleRecipeLookupHandler.ItemChemicalRecipeLookupHandler;
import mekanism.common.recipe.lookup.IRecipeLookupHandler.ConstantUsageRecipeLookupHandler;
import mekanism.common.tile.machine.TileEntityPaintingMachine;
import mekanism.common.tile.prefab.TileEntityProgressMachine;
import mekanism.common.upgrade.AdvancedMachineUpgradeData;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = TileEntityPaintingMachine.class, remap = false)
public abstract class MixinTileEntityPaintingMachine extends TileEntityProgressMachine<ItemStackChemicalToItemStackRecipe> implements ConstantUsageRecipeLookupHandler, ItemChemicalRecipeLookupHandler<ItemStackChemicalToItemStackRecipe> {

    @Shadow
    private int usedSoFar;
    @Shadow
    private MachineEnergyContainer<TileEntityPaintingMachine> energyContainer;
    @Shadow
    public IChemicalTank pigmentTank;
    @Shadow
    ChemicalInventorySlot pigmentInputSlot;
    @Shadow
    InputInventorySlot inputSlot;
    @Shadow
    OutputInventorySlot outputSlot;
    @Shadow
    EnergyInventorySlot energySlot;

    protected MixinTileEntityPaintingMachine(Holder<Block> blockProvider, BlockPos pos, BlockState state, List<CachedRecipe.OperationTracker.RecipeError> errorTypes, int baseTicksRequired) {
        super(blockProvider, pos, state, errorTypes, baseTicksRequired);
    }

    @Unique
    @Override
    public boolean isConfigurationDataCompatible(Block type) {
        return super.isConfigurationDataCompatible(type) || MoreMachineUtils.isSameAFTypeFactory(getBlockHolder(), type);
    }

    @Override
    public @Nullable AdvancedMachineUpgradeData getUpgradeData(HolderLookup.Provider provider) {
        return new AdvancedMachineUpgradeData(provider, redstone, getControlType(), energyContainer, getOperatingTicks(),
                usedSoFar, pigmentTank, pigmentInputSlot, energySlot, inputSlot, outputSlot, getComponents(), problemPath());
    }
}
