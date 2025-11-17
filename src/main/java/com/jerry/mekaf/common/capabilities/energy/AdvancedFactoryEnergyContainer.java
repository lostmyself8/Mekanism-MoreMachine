package com.jerry.mekaf.common.capabilities.energy;

import com.jerry.mekaf.common.tile.base.TileEntityAdvancedFactoryBase;

import mekanism.api.AutomationType;
import mekanism.api.IContentsListener;
import mekanism.api.math.FloatingLong;
import mekanism.common.block.attribute.AttributeEnergy;
import mekanism.common.capabilities.energy.MachineEnergyContainer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class AdvancedFactoryEnergyContainer extends MachineEnergyContainer<TileEntityAdvancedFactoryBase<?>> {

    public static AdvancedFactoryEnergyContainer input(TileEntityAdvancedFactoryBase<?> tile, @Nullable IContentsListener listener) {
        AttributeEnergy electricBlock = validateBlock(tile);
        return new AdvancedFactoryEnergyContainer(electricBlock.getStorage(), electricBlock.getUsage(), notExternal, alwaysTrue, tile, listener);
    }

    private AdvancedFactoryEnergyContainer(FloatingLong maxEnergy, FloatingLong energyPerTick, Predicate<@NotNull AutomationType> canExtract,
                                           Predicate<@NotNull AutomationType> canInsert, TileEntityAdvancedFactoryBase<?> tile, @Nullable IContentsListener listener) {
        super(maxEnergy, energyPerTick, canExtract, canInsert, tile, listener);
    }

    @Override
    public @NotNull FloatingLong getBaseEnergyPerTick() {
        return super.getBaseEnergyPerTick().add(tile.getRecipeEnergyRequired());
    }
}
