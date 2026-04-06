package com.jerry.mekmm.mixin.tile;

import com.jerry.mekaf.common.upgrade.ItemToFluidUpgradeData;

import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.ItemStackToFluidRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.tile.machine.TileEntityNutritionalLiquifier;
import mekanism.common.tile.prefab.TileEntityProgressMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = TileEntityNutritionalLiquifier.class, remap = false)
public abstract class MixinTileEntityNutritionalLiquifier extends TileEntityProgressMachine<ItemStackToFluidRecipe> {

    @Shadow
    public IExtendedFluidTank fluidTank;
    @Shadow
    InputInventorySlot inputSlot;
    @Shadow
    EnergyInventorySlot energySlot;

    protected MixinTileEntityNutritionalLiquifier(IBlockProvider blockProvider, BlockPos pos, BlockState state, List<CachedRecipe.OperationTracker.RecipeError> errorTypes, int baseTicksRequired) {
        super(blockProvider, pos, state, errorTypes, baseTicksRequired);
    }

    @Shadow
    public abstract MachineEnergyContainer<TileEntityNutritionalLiquifier> getEnergyContainer();

    @Unique
    @Override
    public boolean isConfigurationDataCompatible(BlockEntityType<?> tileType) {
        return super.isConfigurationDataCompatible(tileType) || MoreMachineUtils.isSameAFTypeFactory(getBlockType(), tileType);
    }

    @Override
    public @Nullable ItemToFluidUpgradeData getUpgradeData() {
        return new ItemToFluidUpgradeData(redstone, getControlType(), getEnergyContainer(), getOperatingTicks(),
                energySlot, inputSlot, fluidTank, getComponents());
    }
}
