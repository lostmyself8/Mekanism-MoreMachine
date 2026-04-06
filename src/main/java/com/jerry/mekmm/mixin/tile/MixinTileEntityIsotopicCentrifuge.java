package com.jerry.mekmm.mixin.tile;

import com.jerry.mekaf.common.upgrade.GasToGasUpgradeData;

import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.GasToGasRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.recipe.lookup.ISingleRecipeLookupHandler.ChemicalRecipeLookupHandler;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.tile.machine.TileEntityIsotopicCentrifuge;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = TileEntityIsotopicCentrifuge.class, remap = false)
public abstract class MixinTileEntityIsotopicCentrifuge extends TileEntityRecipeMachine<GasToGasRecipe> implements IBoundingBlock, ChemicalRecipeLookupHandler<Gas, GasStack, GasToGasRecipe> {

    @Shadow
    public IGasTank inputTank;
    @Shadow
    public IGasTank outputTank;
    @Shadow
    EnergyInventorySlot energySlot;

    protected MixinTileEntityIsotopicCentrifuge(IBlockProvider blockProvider, BlockPos pos, BlockState state, List<CachedRecipe.OperationTracker.RecipeError> errorTypes) {
        super(blockProvider, pos, state, errorTypes);
    }

    @Shadow
    public abstract MachineEnergyContainer<TileEntityIsotopicCentrifuge> getEnergyContainer();

    @Unique
    @Override
    public boolean isConfigurationDataCompatible(BlockEntityType<?> tileType) {
        return super.isConfigurationDataCompatible(tileType) || MoreMachineUtils.isSameAFTypeFactory(getBlockType(), tileType);
    }

    @Override
    public @Nullable GasToGasUpgradeData getUpgradeData() {
        return new GasToGasUpgradeData(redstone, getControlType(), getEnergyContainer(),
                0, energySlot, inputTank, outputTank, getComponents());
    }
}
