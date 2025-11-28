package com.jerry.mekaf.common.content.blocktype;

import com.jerry.mekaf.common.registries.AdvancedFactoryBlockTypes;

import com.jerry.mekmm.common.MoreMachineLang;
import com.jerry.mekmm.common.content.blocktype.MoreMachineMachine;

import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.text.IHasTranslationKey;
import mekanism.api.text.ILangEntry;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registries.MekanismBlocks;

import lombok.Getter;

import java.util.Locale;
import java.util.function.Supplier;

@NothingNullByDefault
public enum AdvancedFactoryType implements IHasTranslationKey.IHasEnumNameTranslationKey {

    // 回旋应该加在这，如果会加的话
    OXIDIZING("oxidizing", MoreMachineLang.OXIDIZING, () -> AdvancedFactoryBlockTypes.CHEMICAL_OXIDIZER, () -> MekanismBlocks.CHEMICAL_OXIDIZER),
    CHEMICAL_INFUSING("chemical_infusing", MoreMachineLang.CHEMICAL_INFUSING, () -> AdvancedFactoryBlockTypes.CHEMICAL_INFUSER, () -> MekanismBlocks.CHEMICAL_INFUSER),
    // 电解分离机
    DISSOLVING("dissolving", MoreMachineLang.DISSOLVING, () -> AdvancedFactoryBlockTypes.CHEMICAL_DISSOLUTION_CHAMBER, () -> MekanismBlocks.CHEMICAL_DISSOLUTION_CHAMBER),
    WASHING("washing", MoreMachineLang.WASHING, () -> AdvancedFactoryBlockTypes.CHEMICAL_WASHER, () -> MekanismBlocks.CHEMICAL_WASHER),
    CRYSTALLIZING("crystallizing", MoreMachineLang.CRYSTALLIZING, () -> AdvancedFactoryBlockTypes.CHEMICAL_CRYSTALLIZER, () -> MekanismBlocks.CHEMICAL_CRYSTALLIZER),
    PRESSURISED_REACTING("pressurised_reacting", MoreMachineLang.PRESSURISED_REACTING, () -> AdvancedFactoryBlockTypes.PRESSURIZED_REACTION_CHAMBER, () -> MekanismBlocks.PRESSURIZED_REACTION_CHAMBER),
    CENTRIFUGING("centrifuging", MoreMachineLang.CENTRIFUGING, () -> AdvancedFactoryBlockTypes.ISOTOPIC_CENTRIFUGE, () -> MekanismBlocks.ISOTOPIC_CENTRIFUGE),
    LIQUIFYING("liquifying", MoreMachineLang.LIQUIFYING, () -> AdvancedFactoryBlockTypes.NUTRITIONAL_LIQUIFIER, () -> MekanismBlocks.NUTRITIONAL_LIQUIFIER);

    @Getter
    private final String registryNameComponent;
    private final ILangEntry langEntry;
    private final Supplier<MoreMachineMachine.MoreMachineFactoryMachine<?>> baseMachine;
    private final Supplier<BlockRegistryObject<?, ?>> baseBlock;

    AdvancedFactoryType(String registryNameComponent, ILangEntry langEntry, Supplier<MoreMachineMachine.MoreMachineFactoryMachine<?>> baseMachine, Supplier<BlockRegistryObject<?, ?>> baseBlock) {
        this.registryNameComponent = registryNameComponent;
        this.langEntry = langEntry;
        this.baseMachine = baseMachine;
        this.baseBlock = baseBlock;
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
