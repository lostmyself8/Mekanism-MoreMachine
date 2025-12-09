package com.jerry.meklm.common.item.block;

import mekanism.api.functions.ConstantPredicates;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.item.interfaces.IItemSustainedInventory;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.StorageUtils;

import net.minecraft.world.item.ItemStack;

import com.jerry.meklm.common.tile.prefab.TileEntityLargeChemicalTank;
import org.jetbrains.annotations.NotNull;

public abstract class ItemBlockLargeChemicalTank<TILE extends TileEntityLargeChemicalTank<?>> extends ItemBlockTooltip<BlockTileModel<TILE, Machine<TILE>>> implements IItemSustainedInventory {

    protected ItemBlockLargeChemicalTank(BlockTileModel<TILE, Machine<TILE>> block) {
        super(block);
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        // No bar for empty containers as bars are drawn on top of stack count number
        return ChemicalUtil.hasGas(stack) ||
                ChemicalUtil.hasChemical(stack, ConstantPredicates.alwaysTrue(), Capabilities.INFUSION_HANDLER) ||
                ChemicalUtil.hasChemical(stack, ConstantPredicates.alwaysTrue(), Capabilities.PIGMENT_HANDLER) ||
                ChemicalUtil.hasChemical(stack, ConstantPredicates.alwaysTrue(), Capabilities.SLURRY_HANDLER);
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return StorageUtils.getBarWidth(stack);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return ChemicalUtil.getRGBDurabilityForDisplay(stack);
    }
}
