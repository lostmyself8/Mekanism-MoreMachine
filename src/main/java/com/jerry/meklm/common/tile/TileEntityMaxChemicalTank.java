package com.jerry.meklm.common.tile;

import com.jerry.mekmm.api.ITileEntityMekanismAccessor;

import mekanism.api.providers.IBlockProvider;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.util.WorldUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.jerry.meklm.common.capabilities.chemical.LargeChemicalTankChemicalTank;
import com.jerry.meklm.common.tier.MaxChemicalTankTier;
import com.jerry.meklm.common.tile.prefab.TileEntityLargeChemicalTank;

public class TileEntityMaxChemicalTank extends TileEntityLargeChemicalTank<MaxChemicalTankTier> {

    public TileEntityMaxChemicalTank(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state);
    }

    @Override
    protected void presetVariables() {
        super.presetVariables();
        tier = Attribute.getTier(getBlockType(), MaxChemicalTankTier.class);
        if (tier != null) {
            chemicalTank = LargeChemicalTankChemicalTank.create(tier, this);
        }
    }

    @Override
    protected BlockEntity ejectTile() {
        return WorldUtils.getTileEntity(getLevel(), getBlockPos().above(2));
    }

    @Override
    protected boolean notChemicalPort(Direction side, Vec3i offset) {
        if (offset.equals(new Vec3i(0, 2, 0))) {
            return side != Direction.UP;
        }
        return true;
    }

    @Override
    protected boolean notItemPort(Direction side, Vec3i offset) {
        if (offset.equals(new Vec3i(0, 2, 0))) {
            return side != Direction.UP;
        }
        return true;
    }
}
