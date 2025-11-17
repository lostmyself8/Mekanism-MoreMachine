package com.jerry.mekaf.common.registries;

import com.jerry.mekaf.common.content.blocktype.AdvancedFactory;
import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType;

import com.jerry.mekmm.common.content.blocktype.MoreMachineMachine.MoreMachineFactoryMachine;
import com.jerry.mekmm.common.content.blocktype.MoreMachineMachine.MoreMachineMachineBuilder;
import com.jerry.mekmm.common.util.MoreMachineEnumUtils;

import mekanism.api.Upgrade;
import mekanism.common.MekanismLang;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.blocktype.BlockShapes;
import mekanism.common.registries.MekanismContainerTypes;
import mekanism.common.registries.MekanismSounds;
import mekanism.common.registries.MekanismTileEntityTypes;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.machine.*;
import mekanism.common.util.EnumUtils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.EnumSet;

public class AdvancedFactoryBlockTypes {

    private AdvancedFactoryBlockTypes() {}

    private static final Table<FactoryTier, AdvancedFactoryType, AdvancedFactory<?>> AF_FACTORIES = HashBasedTable.create();

    // Chemical Oxidizer
    public static final MoreMachineFactoryMachine<TileEntityChemicalOxidizer> CHEMICAL_OXIDIZER = MoreMachineMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.CHEMICAL_OXIDIZER, MekanismLang.DESCRIPTION_CHEMICAL_OXIDIZER, AdvancedFactoryType.OXIDIZING)
            .withGui(() -> MekanismContainerTypes.CHEMICAL_OXIDIZER)
            .withSound(MekanismSounds.CHEMICAL_OXIDIZER)
            .withEnergyConfig(MekanismConfig.usage.oxidationChamber, MekanismConfig.storage.oxidationChamber)
            .withCustomShape(BlockShapes.CHEMICAL_OXIDIZER)
            .withComputerSupport("chemicalOxidizer")
            .build();
    // Chemical Infuser
    public static final MoreMachineFactoryMachine<TileEntityChemicalInfuser> CHEMICAL_INFUSER = MoreMachineMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.CHEMICAL_INFUSER, MekanismLang.DESCRIPTION_CHEMICAL_INFUSER, AdvancedFactoryType.CHEMICAL_INFUSING)
            .withGui(() -> MekanismContainerTypes.CHEMICAL_INFUSER)
            .withSound(MekanismSounds.CHEMICAL_INFUSER)
            .withEnergyConfig(MekanismConfig.usage.chemicalInfuser, MekanismConfig.storage.chemicalInfuser)
            .withCustomShape(BlockShapes.CHEMICAL_INFUSER)
            .withComputerSupport("chemicalInfuser")
            .build();
    // Chemical Dissolution Chamber
    public static final MoreMachineFactoryMachine<TileEntityChemicalDissolutionChamber> CHEMICAL_DISSOLUTION_CHAMBER = MoreMachineMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.CHEMICAL_DISSOLUTION_CHAMBER, MekanismLang.DESCRIPTION_CHEMICAL_DISSOLUTION_CHAMBER, AdvancedFactoryType.DISSOLVING)
            .withGui(() -> MekanismContainerTypes.CHEMICAL_DISSOLUTION_CHAMBER)
            .withSound(MekanismSounds.CHEMICAL_DISSOLUTION_CHAMBER)
            .withEnergyConfig(MekanismConfig.usage.chemicalDissolutionChamber, MekanismConfig.storage.chemicalDissolutionChamber)
            .withSupportedUpgrades(EnumSet.of(Upgrade.SPEED, Upgrade.ENERGY, Upgrade.MUFFLING, Upgrade.GAS))
            .withCustomShape(BlockShapes.CHEMICAL_DISSOLUTION_CHAMBER)
            .withComputerSupport("chemicalDissolutionChamber")
            .build();
    // Chemical Washer
    public static final MoreMachineFactoryMachine<TileEntityChemicalWasher> CHEMICAL_WASHER = MoreMachineMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.CHEMICAL_WASHER, MekanismLang.DESCRIPTION_CHEMICAL_WASHER, AdvancedFactoryType.WASHING)
            .withGui(() -> MekanismContainerTypes.CHEMICAL_WASHER)
            .withSound(MekanismSounds.CHEMICAL_WASHER)
            .withEnergyConfig(MekanismConfig.usage.chemicalWasher, MekanismConfig.storage.chemicalWasher)
            .withCustomShape(BlockShapes.CHEMICAL_WASHER)
            .withComputerSupport("chemicalWasher")
            .build();
    // Chemical Crystallizer
    public static final MoreMachineFactoryMachine<TileEntityChemicalCrystallizer> CHEMICAL_CRYSTALLIZER = MoreMachineMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.CHEMICAL_CRYSTALLIZER, MekanismLang.DESCRIPTION_CHEMICAL_CRYSTALLIZER, AdvancedFactoryType.CRYSTALLIZING)
            .withGui(() -> MekanismContainerTypes.CHEMICAL_CRYSTALLIZER)
            .withSound(MekanismSounds.CHEMICAL_CRYSTALLIZER)
            .withEnergyConfig(MekanismConfig.usage.chemicalCrystallizer, MekanismConfig.storage.chemicalCrystallizer)
            .withCustomShape(BlockShapes.CHEMICAL_CRYSTALLIZER)
            .withComputerSupport("chemicalCrystallizer")
            .build();
    // Pressurized Reaction Chamber
    public static final MoreMachineFactoryMachine<TileEntityPressurizedReactionChamber> PRESSURIZED_REACTION_CHAMBER = MoreMachineMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.PRESSURIZED_REACTION_CHAMBER, MekanismLang.DESCRIPTION_PRESSURIZED_REACTION_CHAMBER, AdvancedFactoryType.PRESSURISED_REACTING)
            .withGui(() -> MekanismContainerTypes.PRESSURIZED_REACTION_CHAMBER)
            .withSound(MekanismSounds.PRESSURIZED_REACTION_CHAMBER)
            .withEnergyConfig(MekanismConfig.usage.pressurizedReactionBase, MekanismConfig.storage.pressurizedReactionBase)
            .withCustomShape(BlockShapes.PRESSURIZED_REACTION_CHAMBER)
            .withComputerSupport("pressurizedReactionChamber")
            .build();
    // Isotopic Centrifuge
    public static final MoreMachineFactoryMachine<TileEntityIsotopicCentrifuge> ISOTOPIC_CENTRIFUGE = MoreMachineMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.ISOTOPIC_CENTRIFUGE, MekanismLang.DESCRIPTION_ISOTOPIC_CENTRIFUGE, AdvancedFactoryType.CENTRIFUGING)
            .withGui(() -> MekanismContainerTypes.ISOTOPIC_CENTRIFUGE)
            .withEnergyConfig(MekanismConfig.usage.isotopicCentrifuge, MekanismConfig.storage.isotopicCentrifuge)
            .withSound(MekanismSounds.ISOTOPIC_CENTRIFUGE)
            .withCustomShape(BlockShapes.ISOTOPIC_CENTRIFUGE)
            .withBounding((pos, state, builder) -> builder.add(pos.above()))
            .withComputerSupport("isotopicCentrifuge")
            .build();
    // Nutritional Liquifier
    public static final MoreMachineFactoryMachine<TileEntityNutritionalLiquifier> NUTRITIONAL_LIQUIFIER = MoreMachineMachineBuilder
            .createAdvancedFactoryMachine(() -> MekanismTileEntityTypes.NUTRITIONAL_LIQUIFIER, MekanismLang.DESCRIPTION_NUTRITIONAL_LIQUIFIER, AdvancedFactoryType.LIQUIFYING)
            .withGui(() -> MekanismContainerTypes.NUTRITIONAL_LIQUIFIER)
            .withEnergyConfig(MekanismConfig.usage.nutritionalLiquifier, MekanismConfig.storage.nutritionalLiquifier)
            .withSound(MekanismSounds.NUTRITIONAL_LIQUIFIER)
            .withComputerSupport("nutritionalLiquifier")
            .build();

    static {
        for (FactoryTier tier : EnumUtils.FACTORY_TIERS) {
            for (AdvancedFactoryType type : MoreMachineEnumUtils.ADVANCED_FACTORY_TYPES) {
                AF_FACTORIES.put(tier, type, AdvancedFactory.AdvancedFactoryBuilder.createAdvancedFactory(() -> AdvancedFactoryTileEntityTypes.getAdvancedFactoryTile(tier, type), type, tier).build());
            }
        }
    }

    public static AdvancedFactory<?> getAdvancedFactory(FactoryTier tier, AdvancedFactoryType type) {
        return AF_FACTORIES.get(tier, type);
    }
}
