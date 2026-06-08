package com.jerry.mekaf.common.registries;

import com.jerry.mekaf.common.content.blocktype.AdvancedFactory;
import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType;

import com.jerry.mekmm.common.content.blocktype.MoreMachineMachine.MoreMachineFactoryMachine;
import com.jerry.mekmm.common.content.blocktype.MoreMachineMachine.MoreMachineMachineBuilder;
import com.jerry.mekmm.common.util.MoreMachineEnumUtils;
import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.AttributeHasBounding;
import mekanism.common.block.attribute.AttributeSideConfig;
import mekanism.common.block.attribute.AttributeUpgradeSupport;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.blocktype.BlockShapes;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.registries.MekanismContainerTypes;
import mekanism.common.registries.MekanismSounds;
import mekanism.common.registries.MekanismTileEntityTypes;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.machine.*;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class AdvancedFactoryBlockTypes {

    private AdvancedFactoryBlockTypes() {}

    private static final Table<FactoryTier, AdvancedFactoryType, AdvancedFactory<?>> AF_FACTORIES = HashBasedTable.create();

    // Chemical Oxidizer
    public static final MoreMachineFactoryMachine<TileEntityChemicalOxidizer> CHEMICAL_OXIDIZER = MoreMachineMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.CHEMICAL_OXIDIZER, MekanismLang.DESCRIPTION_CHEMICAL_OXIDIZER, AdvancedFactoryType.OXIDIZING)
            .withGui(() -> MekanismContainerTypes.CHEMICAL_OXIDIZER)
            .withSound(MekanismSounds.CHEMICAL_OXIDIZER)
            .withEnergyUsage(MekanismConfig.usage.chemicalOxidizer)
            .with(AttributeSideConfig.ADVANCED_ELECTRIC_MACHINE)
            .withCustomShape(BlockShapes.CHEMICAL_OXIDIZER)
            .withComputerSupport("chemicalOxidizer")
            .build();

    // Chemical Dissolution Chamber
    public static final MoreMachineFactoryMachine<TileEntityChemicalDissolutionChamber> CHEMICAL_DISSOLUTION_CHAMBER = MoreMachineMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.CHEMICAL_DISSOLUTION_CHAMBER, MekanismLang.DESCRIPTION_CHEMICAL_DISSOLUTION_CHAMBER, AdvancedFactoryType.DISSOLVING)
            .withGui(() -> MekanismContainerTypes.CHEMICAL_DISSOLUTION_CHAMBER)
            .withSound(MekanismSounds.CHEMICAL_DISSOLUTION_CHAMBER)
            .withEnergyUsage(MekanismConfig.usage.chemicalDissolutionChamber)
            .with(AttributeUpgradeSupport.DEFAULT_ADVANCED_MACHINE_UPGRADES)
            .with(AttributeSideConfig.ADVANCED_ELECTRIC_MACHINE)
            .withCustomShape(BlockShapes.CHEMICAL_DISSOLUTION_CHAMBER)
            .withComputerSupport("chemicalDissolutionChamber")
            .build();

    // Chemical Washer
    public static final MoreMachineFactoryMachine<TileEntityChemicalWasher> CHEMICAL_WASHER = MoreMachineMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.CHEMICAL_WASHER, MekanismLang.DESCRIPTION_CHEMICAL_WASHER, AdvancedFactoryType.WASHING)
            .withGui(() -> MekanismContainerTypes.CHEMICAL_WASHER)
            .withSound(MekanismSounds.CHEMICAL_WASHER)
            .withEnergyUsage(MekanismConfig.usage.chemicalWasher)
            .withSideConfig(TransmissionType.CHEMICAL, TransmissionType.FLUID, TransmissionType.ITEM, TransmissionType.ENERGY)
            .withCustomShape(BlockShapes.CHEMICAL_WASHER)
            .withComputerSupport("chemicalWasher")
            .build();

    // Pressurized Reaction Chamber
    public static final MoreMachineFactoryMachine<TileEntityPressurizedReactionChamber> PRESSURIZED_REACTION_CHAMBER = MoreMachineMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.PRESSURIZED_REACTION_CHAMBER, MekanismLang.DESCRIPTION_PRESSURIZED_REACTION_CHAMBER, AdvancedFactoryType.PRESSURISED_REACTING)
            .withGui(() -> MekanismContainerTypes.PRESSURIZED_REACTION_CHAMBER)
            .withSound(MekanismSounds.PRESSURIZED_REACTION_CHAMBER)
            .withEnergyUsage(MekanismConfig.usage.pressurizedReactionBase)
            .withSideConfig(TransmissionType.ITEM, TransmissionType.CHEMICAL, TransmissionType.FLUID, TransmissionType.ENERGY)
            .withCustomShape(BlockShapes.PRESSURIZED_REACTION_CHAMBER)
            .withComputerSupport("pressurizedReactionChamber")
            .build();

    // Chemical Crystallizer
    public static final MoreMachineFactoryMachine<TileEntityChemicalCrystallizer> CHEMICAL_CRYSTALLIZER = MoreMachineMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.CHEMICAL_CRYSTALLIZER, MekanismLang.DESCRIPTION_CHEMICAL_CRYSTALLIZER, AdvancedFactoryType.CRYSTALLIZING)
            .withGui(() -> MekanismContainerTypes.CHEMICAL_CRYSTALLIZER)
            .withSound(MekanismSounds.CHEMICAL_CRYSTALLIZER)
            .withEnergyUsage(MekanismConfig.usage.chemicalCrystallizer)
            .with(AttributeSideConfig.ADVANCED_ELECTRIC_MACHINE)
            .withCustomShape(BlockShapes.CHEMICAL_CRYSTALLIZER)
            .withComputerSupport("chemicalCrystallizer")
            .build();

    // Isotopic Centrifuge
    public static final MoreMachineFactoryMachine<TileEntityIsotopicCentrifuge> ISOTOPIC_CENTRIFUGE = MoreMachineMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.ISOTOPIC_CENTRIFUGE, MekanismLang.DESCRIPTION_ISOTOPIC_CENTRIFUGE, AdvancedFactoryType.CENTRIFUGING)
            .withGui(() -> MekanismContainerTypes.ISOTOPIC_CENTRIFUGE)
            .withEnergyUsage(MekanismConfig.usage.isotopicCentrifuge)
            .withSideConfig(TransmissionType.CHEMICAL, TransmissionType.ITEM, TransmissionType.ENERGY)
            .withSound(MekanismSounds.ISOTOPIC_CENTRIFUGE)
            .withCustomShape(BlockShapes.ISOTOPIC_CENTRIFUGE)
            .with(AttributeHasBounding.ABOVE_ONLY)
            .withComputerSupport("isotopicCentrifuge")
            .build();

    // Nutritional Liquifier
    public static final MoreMachineFactoryMachine<TileEntityNutritionalLiquifier> NUTRITIONAL_LIQUIFIER = MoreMachineMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.NUTRITIONAL_LIQUIFIER, MekanismLang.DESCRIPTION_NUTRITIONAL_LIQUIFIER, AdvancedFactoryType.LIQUIFYING)
            .withGui(() -> MekanismContainerTypes.NUTRITIONAL_LIQUIFIER)
            .withEnergyUsage(MekanismConfig.usage.nutritionalLiquifier)
            .withSideConfig(TransmissionType.ITEM, TransmissionType.FLUID, TransmissionType.ENERGY)
            .withSound(MekanismSounds.NUTRITIONAL_LIQUIFIER)
            .withComputerSupport("nutritionalLiquifier")
            .build();

    // Pigment Extractor
    public static final MoreMachineFactoryMachine<TileEntityPigmentExtractor> PIGMENT_EXTRACTOR = MoreMachineMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.PIGMENT_EXTRACTOR, MekanismLang.DESCRIPTION_PIGMENT_EXTRACTOR, AdvancedFactoryType.PIGMENT_EXTRACTING)
            .withGui(() -> MekanismContainerTypes.PIGMENT_EXTRACTOR)
            .withSound(MekanismSounds.PIGMENT_EXTRACTOR)
            .withEnergyUsage(MekanismConfig.usage.pigmentExtractor)
            .with(AttributeUpgradeSupport.DEFAULT_MACHINE_UPGRADES)
            .with(AttributeSideConfig.ADVANCED_ELECTRIC_MACHINE)
            .withComputerSupport("pigmentExtractor")
            .build();

    // Painting Machine
    public static final MoreMachineFactoryMachine<TileEntityPaintingMachine> PAINTING_MACHINE = MoreMachineMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.PAINTING_MACHINE, MekanismLang.DESCRIPTION_PAINTING_MACHINE, AdvancedFactoryType.PAINTING)
            .withGui(() -> MekanismContainerTypes.PAINTING_MACHINE)
            .withSound(MekanismSounds.PAINTING_MACHINE)
            .withEnergyUsage(MekanismConfig.usage.paintingMachine)
            .with(AttributeUpgradeSupport.DEFAULT_MACHINE_UPGRADES)
            .with(AttributeSideConfig.ADVANCED_ELECTRIC_MACHINE)
            .withComputerSupport("paintingMachine")
            .build();

    static {
        for (FactoryTier tier : MoreMachineUtils.getFactoryTier()) {
            for (AdvancedFactoryType type : MoreMachineEnumUtils.ADVANCED_FACTORY_TYPES) {
                AF_FACTORIES.put(tier, type, AdvancedFactory.AdvancedFactoryBuilder.createAdvancedFactory(() -> AdvancedFactoryTileEntityTypes.getAdvancedFactoryTile(tier, type), type, tier).build());
            }
        }
    }

    public static AdvancedFactory<?> getAdvancedFactory(FactoryTier tier, AdvancedFactoryType type) {
        return AF_FACTORIES.get(tier, type);
    }
}
