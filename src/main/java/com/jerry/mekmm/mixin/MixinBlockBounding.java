package com.jerry.mekmm.mixin;

import com.jerry.meklg.common.content.blocktype.LargeGeneratorBlockShapes;
import com.jerry.meklg.common.registries.LargeGeneratorBlocks;

import mekanism.common.block.BlockBounding;
import mekanism.common.block.attribute.Attribute;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockBounding.class, remap = false)
public class MixinBlockBounding {

    @Inject(method = {
            "getShape",
            "getCollisionShape",
            "getVisualShape"
    }, at = @At("HEAD"), cancellable = true)
    private void getLargeWindGeneratorPartShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context,
          CallbackInfoReturnable<VoxelShape> cir) {
        mekmm$setLargeWindGeneratorPartShape(world, pos, cir);
    }

    @Inject(method = {
            "getBlockSupportShape",
            "getInteractionShape"
    }, at = @At("HEAD"), cancellable = true)
    private void getLargeWindGeneratorPartShape(BlockState state, BlockGetter world, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        mekmm$setLargeWindGeneratorPartShape(world, pos, cir);
    }

    private void mekmm$setLargeWindGeneratorPartShape(BlockGetter world, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        BlockPos mainPos = BlockBounding.getMainBlockPos(world, pos);
        if (mainPos == null) {
            return;
        }
        BlockState mainState = world.getBlockState(mainPos);
        if (!mainState.is(LargeGeneratorBlocks.LARGE_WIND_GENERATOR)) {
            return;
        }
        Direction facing = Attribute.getFacing(mainState);
        if (facing == null) {
            return;
        }
        VoxelShape shape = LargeGeneratorBlockShapes.getLargeWindGeneratorPartShape(facing, pos.subtract(mainPos));
        if (shape != null) {
            cir.setReturnValue(shape);
        }
    }
}
