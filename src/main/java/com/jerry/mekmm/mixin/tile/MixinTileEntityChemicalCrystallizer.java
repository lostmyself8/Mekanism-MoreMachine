package com.jerry.mekmm.mixin.tile;

import com.jerry.mekaf.common.upgrade.MergedToItemUpgradeData;

import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.ChemicalCrystallizerRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.OutputInventorySlot;
import mekanism.common.tile.machine.TileEntityChemicalCrystallizer;
import mekanism.common.tile.prefab.TileEntityProgressMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = TileEntityChemicalCrystallizer.class, remap = false)
public abstract class MixinTileEntityChemicalCrystallizer extends TileEntityProgressMachine<ChemicalCrystallizerRecipe> {

    @Shadow
    public MergedChemicalTank inputTank;
    @Shadow
    OutputInventorySlot outputSlot;
    @Shadow
    EnergyInventorySlot energySlot;

    protected MixinTileEntityChemicalCrystallizer(IBlockProvider blockProvider, BlockPos pos, BlockState state, List<CachedRecipe.OperationTracker.RecipeError> errorTypes, int baseTicksRequired) {
        super(blockProvider, pos, state, errorTypes, baseTicksRequired);
    }

    @Shadow
    public abstract MachineEnergyContainer<TileEntityChemicalCrystallizer> getEnergyContainer();

    @Unique
    @Override
    public boolean isConfigurationDataCompatible(BlockEntityType<?> tileType) {
        return super.isConfigurationDataCompatible(tileType) || MoreMachineUtils.isSameAFTypeFactory(getBlockType(), tileType);
    }

    @Override
    public @Nullable MergedToItemUpgradeData getUpgradeData() {
        return new MergedToItemUpgradeData(redstone, getControlType(), getEnergyContainer(),
                getOperatingTicks(), energySlot, inputTank, outputSlot, getComponents());
    }
}
