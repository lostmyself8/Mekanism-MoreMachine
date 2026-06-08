package com.jerry.mekmm.common.integration.computer;

import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod.WrappingComputerMethodHelp;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod.WrappingComputerMethodIndex;

public class ComputerEnergyContainerWrapper extends SpecialComputerMethodWrapper {

    @WrappingComputerMethodIndex(0)
    @WrappingComputerMethodHelp("Get the stored of the %s.")
    public static long getEnergy(MachineEnergyContainer<?> container) {
        return container.getAmountAsLong();
    }

    @WrappingComputerMethodIndex(1)
    @WrappingComputerMethodHelp("Get the capacity of the %s.")
    public static long getCapacity(MachineEnergyContainer<?> container) {
        return container.getCapacityAsLong();
    }

    @WrappingComputerMethodIndex(2)
    @WrappingComputerMethodHelp("Get the amount needed to fill the %s.")
    public static long getNeeded(MachineEnergyContainer<?> container) {
        return Math.max(0, container.getCapacityAsLong() - container.getAmountAsLong());
    }

    @WrappingComputerMethodIndex(3)
    @WrappingComputerMethodHelp("Get the filled percentage of the %s.")
    public static double getFilledPercentage(MachineEnergyContainer<?> container) {
        return container.getAmountAsLong() / (double) container.getCapacityAsLong();
    }
}
