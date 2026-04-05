package com.jerry.mekmm.mixin.tile;

import com.jerry.mekaf.common.upgrade.ItemToPigmentUpgradeData;

import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.ItemStackToPigmentRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.inventory.slot.InputInventorySlot;
import mekanism.common.recipe.lookup.ISingleRecipeLookupHandler.ItemRecipeLookupHandler;
import mekanism.common.tile.machine.TileEntityPigmentExtractor;
import mekanism.common.tile.prefab.TileEntityProgressMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = TileEntityPigmentExtractor.class, remap = false)
public abstract class MixinTileEntityPigmentExtractor extends TileEntityProgressMachine<ItemStackToPigmentRecipe> implements ItemRecipeLookupHandler<ItemStackToPigmentRecipe> {

    @Shadow
    public IPigmentTank pigmentTank;
    @Shadow
    InputInventorySlot inputSlot;
    @Shadow
    EnergyInventorySlot energySlot;

    protected MixinTileEntityPigmentExtractor(IBlockProvider blockProvider, BlockPos pos, BlockState state, List<CachedRecipe.OperationTracker.RecipeError> errorTypes, int baseTicksRequired) {
        super(blockProvider, pos, state, errorTypes, baseTicksRequired);
    }

    @Shadow
    public abstract MachineEnergyContainer<TileEntityPigmentExtractor> getEnergyContainer();

    @Unique
    @Override
    public boolean isConfigurationDataCompatible(BlockEntityType<?> tileType) {
        return super.isConfigurationDataCompatible(tileType) || MoreMachineUtils.isSameAFTypeFactory(getBlockType(), tileType);
    }

    @Override
    public @Nullable ItemToPigmentUpgradeData getUpgradeData() {
        return new ItemToPigmentUpgradeData(redstone, getControlType(), getEnergyContainer(),
                getOperatingTicks(), energySlot, inputSlot, pigmentTank, getComponents());
    }
}
