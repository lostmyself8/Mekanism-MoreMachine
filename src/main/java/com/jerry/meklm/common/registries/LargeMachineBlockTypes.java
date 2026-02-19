package com.jerry.meklm.common.registries;

import com.jerry.mekmm.common.block.attribute.MoreMachineBounding;

import mekanism.api.functions.TriConsumer;
import mekanism.common.MekanismLang;
import mekanism.common.block.attribute.*;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.content.blocktype.Machine.MachineBuilder;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.registries.MekanismSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import com.jerry.meklm.common.content.blocktype.LargeMachineBlockShapes;
import com.jerry.meklm.common.tier.ILargeTankTier;
import com.jerry.meklm.common.tier.MaxChemicalTankTier;
import com.jerry.meklm.common.tier.MidChemicalTankTier;
import com.jerry.meklm.common.tile.TileEntityMaxChemicalTank;
import com.jerry.meklm.common.tile.TileEntityMidChemicalTank;
import com.jerry.meklm.common.tile.machine.TileEntityLargeChemicalInfuser;
import com.jerry.meklm.common.tile.machine.TileEntityLargeElectrolyticSeparator;
import com.jerry.meklm.common.tile.machine.TileEntityLargeRotaryCondensentrator;
import com.jerry.meklm.common.tile.machine.TileEntityLargeSolarNeutronActivator;
import com.jerry.meklm.common.tile.prefab.TileEntityLargeChemicalTank;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class LargeMachineBlockTypes {

    private LargeMachineBlockTypes() {}

    // Mid-Chemical Tanks
    public static final Machine<TileEntityMidChemicalTank> BASIC_MID_CHEMICAL_TANK = createLargeChemicalTank(MidChemicalTankTier.BASIC, () -> LargeMachineTileEntityTypes.BASIC_MID_CHEMICAL_TANK, () -> LargeMachineBlocks.ADVANCED_MID_CHEMICAL_TANK);
    public static final Machine<TileEntityMidChemicalTank> ADVANCED_MID_CHEMICAL_TANK = createLargeChemicalTank(MidChemicalTankTier.ADVANCED, () -> LargeMachineTileEntityTypes.ADVANCED_MID_CHEMICAL_TANK, () -> LargeMachineBlocks.ELITE_MID_CHEMICAL_TANK);
    public static final Machine<TileEntityMidChemicalTank> ELITE_MID_CHEMICAL_TANK = createLargeChemicalTank(MidChemicalTankTier.ELITE, () -> LargeMachineTileEntityTypes.ELITE_MID_CHEMICAL_TANK, () -> LargeMachineBlocks.ULTIMATE_MID_CHEMICAL_TANK);
    public static final Machine<TileEntityMidChemicalTank> ULTIMATE_MID_CHEMICAL_TANK = createLargeChemicalTank(MidChemicalTankTier.ULTIMATE, () -> LargeMachineTileEntityTypes.ULTIMATE_MID_CHEMICAL_TANK, null);

    // Max-Chemical Tanks
    public static final Machine<TileEntityMaxChemicalTank> BASIC_MAX_CHEMICAL_TANK = createLargeChemicalTank(MaxChemicalTankTier.BASIC, () -> LargeMachineTileEntityTypes.BASIC_MAX_CHEMICAL_TANK, () -> LargeMachineBlocks.ADVANCED_MAX_CHEMICAL_TANK);
    public static final Machine<TileEntityMaxChemicalTank> ADVANCED_MAX_CHEMICAL_TANK = createLargeChemicalTank(MaxChemicalTankTier.ADVANCED, () -> LargeMachineTileEntityTypes.ADVANCED_MAX_CHEMICAL_TANK, () -> LargeMachineBlocks.ELITE_MAX_CHEMICAL_TANK);
    public static final Machine<TileEntityMaxChemicalTank> ELITE_MAX_CHEMICAL_TANK = createLargeChemicalTank(MaxChemicalTankTier.ELITE, () -> LargeMachineTileEntityTypes.ELITE_MAX_CHEMICAL_TANK, () -> LargeMachineBlocks.ULTIMATE_MAX_CHEMICAL_TANK);
    public static final Machine<TileEntityMaxChemicalTank> ULTIMATE_MAX_CHEMICAL_TANK = createLargeChemicalTank(MaxChemicalTankTier.ULTIMATE, () -> LargeMachineTileEntityTypes.ULTIMATE_MAX_CHEMICAL_TANK, null);

    // Rotary Condensentrator
    public static final Machine<TileEntityLargeRotaryCondensentrator> LARGE_ROTARY_CONDENSENTRATOR = MachineBuilder
            .createMachine(() -> LargeMachineTileEntityTypes.LARGE_ROTARY_CONDENSENTRATOR, MekanismLang.DESCRIPTION_ROTARY_CONDENSENTRATOR)
            .withGui(() -> LargeMachineContainerTypes.LARGE_ROTARY_CONDENSENTRATOR)
            .withSound(MekanismSounds.ROTARY_CONDENSENTRATOR)
            .withEnergyConfig(MekanismConfig.usage.rotaryCondensentrator, MekanismConfig.storage.rotaryCondensentrator)
            .withCustomShape(LargeMachineBlockShapes.LARGE_ROTARY_CONDENSENTRATOR)
            .with(AttributeCustomSelectionBox.JSON)
            .withBounding(MoreMachineBounding.FULL_JAVA_ENTITY)
            .withComputerSupport("largeRotaryCondensentrator")
            .build();
    // Chemical Infuser
    public static final Machine<TileEntityLargeChemicalInfuser> LARGE_CHEMICAL_INFUSER = MachineBuilder
            .createMachine(() -> LargeMachineTileEntityTypes.LARGE_CHEMICAL_INFUSER, MekanismLang.DESCRIPTION_CHEMICAL_INFUSER)
            .withGui(() -> LargeMachineContainerTypes.LARGE_CHEMICAL_INFUSER)
            .withSound(MekanismSounds.CHEMICAL_INFUSER)
            .withEnergyConfig(MekanismConfig.usage.chemicalInfuser, MekanismConfig.storage.chemicalInfuser)
            .withCustomShape(LargeMachineBlockShapes.LARGE_CHEMICAL_INFUSER)
            .with(AttributeCustomSelectionBox.JSON)
            .withBounding(MoreMachineBounding.FULL_JAVA_ENTITY_BUT_TOP_BACK_2X3)
            .withComputerSupport("largeChemicalInfuser")
            .build();
    // Electrolytic Separator
    public static final Machine<TileEntityLargeElectrolyticSeparator> LARGE_ELECTROLYTIC_SEPARATOR = MachineBuilder
            .createMachine(() -> LargeMachineTileEntityTypes.LARGE_ELECTROLYTIC_SEPARATOR, MekanismLang.DESCRIPTION_ELECTROLYTIC_SEPARATOR)
            .withGui(() -> LargeMachineContainerTypes.LARGE_ELECTROLYTIC_SEPARATOR)
            .withSound(MekanismSounds.ELECTROLYTIC_SEPARATOR)
            .withEnergyConfig(() -> MekanismConfig.general.FROM_H2.get().multiply(2), MekanismConfig.storage.electrolyticSeparator)
            .withCustomShape(LargeMachineBlockShapes.LARGE_ELECTROLYTIC_SEPARATOR)
            .with(AttributeCustomSelectionBox.JSON)
            .withBounding(MoreMachineBounding.FULL_3X3X2)
            .withComputerSupport("largeElectrolyticSeparator")
            .build();
    // Solar Neutron Activator
    public static final Machine<TileEntityLargeSolarNeutronActivator> LARGE_SOLAR_NEUTRON_ACTIVATOR = MachineBuilder
            .createMachine(() -> LargeMachineTileEntityTypes.LARGE_SOLAR_NEUTRON_ACTIVATOR, MekanismLang.DESCRIPTION_SOLAR_NEUTRON_ACTIVATOR)
            .withGui(() -> LargeMachineContainerTypes.LARGE_SOLAR_NEUTRON_ACTIVATOR)
            .without(AttributeParticleFX.class, AttributeUpgradeSupport.class)
            .withCustomShape(LargeMachineBlockShapes.LARGE_SOLAR_NEUTRON_ACTIVATOR)
            .with(AttributeCustomSelectionBox.JSON)
            .withBounding(MoreMachineBounding.FULL_JAVA_ENTITY)
            .withComputerSupport("largeSolarNeutronActivator")
            .replace(Attributes.ACTIVE)
            .build();

    private static <TILE extends TileEntityLargeChemicalTank<?>> Machine<TILE> createLargeChemicalTank(ILargeTankTier tier, Supplier<TileEntityTypeRegistryObject<TILE>> tile, Supplier<BlockRegistryObject<?, ?>> upgradeBlock) {
        return MachineBuilder.createMachine(tile, MekanismLang.DESCRIPTION_CHEMICAL_TANK)
                .withGui(() -> LargeMachineContainerTypes.LARGE_CHEMICAL_TANK)
                .withCustomShape(LargeMachineBlockShapes.getTankShape(tier))
                .with(new AttributeTier<>(tier), new AttributeUpgradeable(upgradeBlock))
                .without(AttributeParticleFX.class, AttributeStateActive.class, AttributeUpgradeSupport.class)
                .withBounding(getBounding(tier))
                .withComputerSupport(tier, tier.type() + "ChemicalTank")
                .build();
    }

    private static TriConsumer<BlockPos, BlockState, Stream.Builder<BlockPos>> getBounding(ILargeTankTier tier) {
        return tier instanceof MidChemicalTankTier ?
                (pos, state, builder) -> builder.add(pos.above(1)) :
                (pos, state, builder) -> {
                    for (int i = 0; i < 2; i++) {
                        builder.add(pos.above(i + 1));
                    }
                };
    }
}
