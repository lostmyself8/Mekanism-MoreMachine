package com.jerry.mekmm.common.content.blocktype;

import com.jerry.mekmm.common.MoreMachineLang;
import com.jerry.mekmm.common.registries.MoreMachineBlockTypes;
import com.jerry.mekmm.common.registries.MoreMachineBlocks;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.text.IHasTranslationKey;
import mekanism.api.text.ILangEntry;
import mekanism.common.registration.impl.BlockRegistryObject;

import java.util.Locale;
import java.util.function.Supplier;

@NothingNullByDefault
public enum MoreMachineFactoryType implements IHasTranslationKey {

    RECYCLING("recycling", MoreMachineLang.RECYCLING, () -> MoreMachineBlockTypes.RECYCLER, () -> MoreMachineBlocks.RECYCLER),
    PLANTING("planting", MoreMachineLang.PLANTING, () -> MoreMachineBlockTypes.PLANTING_STATION, () -> MoreMachineBlocks.PLANTING_STATION),
    CNC_STAMPING("stamping", MoreMachineLang.STAMPING, () -> MoreMachineBlockTypes.CNC_STAMPER, () -> MoreMachineBlocks.CNC_STAMPER),
    CNC_LATHING("lathing", MoreMachineLang.LATHING, () -> MoreMachineBlockTypes.CNC_LATHE, () -> MoreMachineBlocks.CNC_LATHE),
    CNC_ROLLING_MILL("rolling_mill", MoreMachineLang.ROLLING_MILL, () -> MoreMachineBlockTypes.CNC_ROLLING_MILL, () -> MoreMachineBlocks.CNC_ROLLING_MILL),
    REPLICATING("replicating", MoreMachineLang.REPLICATING, () -> MoreMachineBlockTypes.REPLICATOR, () -> MoreMachineBlocks.REPLICATOR);

    private final String registryNameComponent;
    private final ILangEntry langEntry;
    private final Supplier<MoreMachineMachine.MoreMachineFactoryMachine<?>> baseMachine;
    private final Supplier<BlockRegistryObject<?, ?>> baseBlock;

    MoreMachineFactoryType(String registryNameComponent, ILangEntry langEntry, Supplier<MoreMachineMachine.MoreMachineFactoryMachine<?>> baseMachine, Supplier<BlockRegistryObject<?, ?>> baseBlock) {
        this.registryNameComponent = registryNameComponent;
        this.langEntry = langEntry;
        this.baseMachine = baseMachine;
        this.baseBlock = baseBlock;
    }

    public String getRegistryNameComponent() {
        return registryNameComponent;
    }

    public String getRegistryNameComponentCapitalized() {
        String name = getRegistryNameComponent();
        return name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
    }

    public MoreMachineMachine.MoreMachineFactoryMachine<?> getBaseMachine() {
        return baseMachine.get();
    }

    public BlockRegistryObject<?, ?> getBaseBlock() {
        return baseBlock.get();
    }

    @Override
    public String getTranslationKey() {
        return langEntry.getTranslationKey();
    }
}
