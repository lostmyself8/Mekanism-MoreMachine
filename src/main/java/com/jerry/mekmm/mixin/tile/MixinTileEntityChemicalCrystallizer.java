package com.jerry.mekmm.mixin.tile;

import com.jerry.mekaf.common.upgrade.ChemicalToItemUpgradeData;

import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.chemical.IChemicalTank;
import mekanism.api.recipes.ChemicalCrystallizerRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.recipe.lookup.ISingleRecipeLookupHandler.ChemicalRecipeLookupHandler;
import mekanism.common.tile.machine.TileEntityChemicalCrystallizer;
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

@Mixin(value = TileEntityChemicalCrystallizer.class, remap = false)
public abstract class MixinTileEntityChemicalCrystallizer extends TileEntityProgressMachine<ChemicalCrystallizerRecipe> implements ChemicalRecipeLookupHandler<ChemicalCrystallizerRecipe> {

    @Shadow
    public IChemicalTank inputTank;
    @Shadow
    OutputInventorySlot outputSlot;
    @Shadow
    EnergyInventorySlot energySlot;

    protected MixinTileEntityChemicalCrystallizer(Holder<Block> blockProvider, BlockPos pos, BlockState state, List<CachedRecipe.OperationTracker.RecipeError> errorTypes, int baseTicksRequired) {
        super(blockProvider, pos, state, errorTypes, baseTicksRequired);
    }

    @Shadow
    public abstract MachineEnergyContainer<TileEntityChemicalCrystallizer> getEnergyContainer();

    @Unique
    @Override
    public boolean isConfigurationDataCompatible(Block type) {
        return super.isConfigurationDataCompatible(type) || MoreMachineUtils.isSameAFTypeFactory(getBlockHolder(), type);
    }

    @Override
    public @Nullable ChemicalToItemUpgradeData getUpgradeData(Provider provider) {
        return new ChemicalToItemUpgradeData(provider, redstone, getControlType(), getEnergyContainer(),
                getOperatingTicks(), energySlot, inputTank, outputSlot, getComponents());
    }
}
