package com.jerry.meklg.common.block;

import com.jerry.mekmm.common.block.attribute.MoreMachineBounding;

import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.generators.common.content.blocktype.Generator;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import com.jerry.meklg.common.tile.generator.TileEntityLargeWindGenerator;

public class BlockLargeWindGenerator extends BlockTileModel<TileEntityLargeWindGenerator, Generator<TileEntityLargeWindGenerator>> {

    public BlockLargeWindGenerator(Generator<TileEntityLargeWindGenerator> type, BlockBehaviour.Properties properties) {
        super(type, properties);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        MoreMachineBounding.LARGE_WIND_GENERATOR.placeBoundingBlocks(level, pos, state);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
        MoreMachineBounding.LARGE_WIND_GENERATOR.removeBoundingBlocks(level, pos, state);
    }
}
