package com.jerry.mekmm.common.block;

import com.jerry.mekmm.common.resource.MoreMachineBlockResourceInfo;

import mekanism.common.block.BlockMekanism;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class BlockMoreMachineResource extends BlockMekanism {

    @NotNull
    private final MoreMachineBlockResourceInfo resource;

    public BlockMoreMachineResource(BlockBehaviour.Properties properties, @NotNull MoreMachineBlockResourceInfo resource) {
        super(resource.modifyProperties(properties.requiresCorrectToolForDrops()));
        this.resource = resource;
    }

    @NotNull
    public MoreMachineBlockResourceInfo getResourceInfo() {
        return resource;
    }

    @Override
    public boolean isPortalFrame(@NonNull BlockState state, @NonNull BlockGetter world, @NonNull BlockPos pos) {
        return resource.isPortalFrame();
    }
}
