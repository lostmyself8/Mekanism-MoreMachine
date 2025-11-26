package com.jerry.meklm.common.tile;

import com.jerry.meklm.common.tier.MaxChemicalTankTier;
import com.jerry.meklm.common.tile.prefab.TileEntityLargeChemicalTank;

import mekanism.common.block.attribute.Attribute;
import mekanism.common.tile.interfaces.IBoundingBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityMaxChemicalTank extends TileEntityLargeChemicalTank<MaxChemicalTankTier> implements IBoundingBlock {

    public TileEntityMaxChemicalTank(Holder<Block> blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @Override
    protected void presetVariables() {
        super.presetVariables();
        tier = Attribute.getTier(getBlockHolder(), MaxChemicalTankTier.class);
    }
}
