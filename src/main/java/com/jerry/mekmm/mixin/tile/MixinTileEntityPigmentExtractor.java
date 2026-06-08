package com.jerry.mekmm.mixin.tile;

import com.jerry.mekaf.common.upgrade.ItemToChemicalUpgradeData;

import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.chemical.IChemicalTank;
import mekanism.api.recipes.ItemStackToChemicalRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.recipe.lookup.ISingleRecipeLookupHandler.ItemRecipeLookupHandler;
import mekanism.common.tile.machine.TileEntityPigmentExtractor;
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

@Mixin(value = TileEntityPigmentExtractor.class, remap = false)
public abstract class MixinTileEntityPigmentExtractor extends TileEntityProgressMachine<ItemStackToChemicalRecipe> implements ItemRecipeLookupHandler<ItemStackToChemicalRecipe> {

    @Shadow
    private MachineEnergyContainer<TileEntityPigmentExtractor> energyContainer;
    @Shadow
    public IChemicalTank pigmentTank;
    @Shadow
    InputInventorySlot inputSlot;
    @Shadow
    EnergyInventorySlot energySlot;

    protected MixinTileEntityPigmentExtractor(Holder<Block> blockProvider, BlockPos pos, BlockState state, List<CachedRecipe.OperationTracker.RecipeError> errorTypes, int baseTicksRequired) {
        super(blockProvider, pos, state, errorTypes, baseTicksRequired);
    }

    @Unique
    @Override
    public boolean isConfigurationDataCompatible(Block type) {
        return super.isConfigurationDataCompatible(type) || MoreMachineUtils.isSameAFTypeFactory(getBlockHolder(), type);
    }

    @Override
    public @Nullable ItemToChemicalUpgradeData getUpgradeData(Provider provider) {
        return new ItemToChemicalUpgradeData(provider, redstone, getControlType(), energyContainer,
                getOperatingTicks(), energySlot, inputSlot, pigmentTank, getComponents(), problemPath());
    }
}
