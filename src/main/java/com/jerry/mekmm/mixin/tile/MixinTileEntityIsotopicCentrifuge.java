package com.jerry.mekmm.mixin.tile;

import com.jerry.mekaf.common.upgrade.ChemicalToChemicalUpgradeData;

import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.chemical.IChemicalTank;
import mekanism.api.recipes.ChemicalToChemicalRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.recipe.lookup.ISingleRecipeLookupHandler.ChemicalRecipeLookupHandler;
import mekanism.common.tile.interfaces.IBoundingBlock;
import mekanism.common.tile.machine.TileEntityIsotopicCentrifuge;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;

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

@Mixin(value = TileEntityIsotopicCentrifuge.class, remap = false)
public abstract class MixinTileEntityIsotopicCentrifuge extends TileEntityRecipeMachine<ChemicalToChemicalRecipe> implements IBoundingBlock, ChemicalRecipeLookupHandler<ChemicalToChemicalRecipe> {

    @Shadow
    private MachineEnergyContainer<TileEntityIsotopicCentrifuge> energyContainer;
    @Shadow
    public IChemicalTank inputTank;
    @Shadow
    public IChemicalTank outputTank;
    @Shadow
    EnergyInventorySlot energySlot;

    protected MixinTileEntityIsotopicCentrifuge(Holder<Block> blockProvider, BlockPos pos, BlockState state, List<CachedRecipe.OperationTracker.RecipeError> errorTypes) {
        super(blockProvider, pos, state, errorTypes);
    }

    @Unique
    @Override
    public boolean isConfigurationDataCompatible(Block type) {
        return super.isConfigurationDataCompatible(type) || MoreMachineUtils.isSameAFTypeFactory(getBlockHolder(), type);
    }

    @Override
    public @Nullable ChemicalToChemicalUpgradeData getUpgradeData(Provider provider) {
        return new ChemicalToChemicalUpgradeData(provider, redstone, getControlType(), energyContainer,
                0, energySlot, inputTank, outputTank, getComponents(), problemPath());
    }
}
