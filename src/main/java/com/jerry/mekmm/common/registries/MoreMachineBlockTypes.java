package com.jerry.mekmm.common.registries;

import com.jerry.mekmm.common.MoreMachineLang;
import com.jerry.mekmm.common.block.attribute.MoreMachineBounding;
import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.content.blocktype.MoreMachineBlockShapes;
import com.jerry.mekmm.common.content.blocktype.MoreMachineFactory;
import com.jerry.mekmm.common.content.blocktype.MoreMachineFactory.MoreMachineFactoryBuilder;
import com.jerry.mekmm.common.content.blocktype.MoreMachineFactoryType;
import com.jerry.mekmm.common.content.blocktype.MoreMachineMachine.MoreMachineFactoryMachine;
import com.jerry.mekmm.common.content.blocktype.MoreMachineMachine.MoreMachineMachineBuilder;
import com.jerry.mekmm.common.tile.TileEntityAuthorDoll;
import com.jerry.mekmm.common.tile.TileEntityModelerDoll;
import com.jerry.mekmm.common.tile.machine.*;
import com.jerry.mekmm.common.tile.machine.TileEntityWirelessChargingStation;
import com.jerry.mekmm.common.tile.machine.TileEntityWirelessTransmissionStation;
import com.jerry.mekmm.common.util.MoreMachineEnumUtils;
import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.Upgrade;
import mekanism.common.block.attribute.*;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.content.blocktype.Machine.MachineBuilder;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.registries.MekanismSounds;
import mekanism.common.tier.FactoryTier;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class MoreMachineBlockTypes {

    private MoreMachineBlockTypes() {}

    private static final Table<FactoryTier, MoreMachineFactoryType, MoreMachineFactory<?>> MM_FACTORIES = HashBasedTable.create();

    // Recycler
    public static final MoreMachineFactoryMachine<TileEntityRecycler> RECYCLER = MoreMachineMachineBuilder
            .createMoreMachineFactoryMachine(() -> MoreMachineTileEntityTypes.RECYCLER, MoreMachineLang.DESCRIPTION_RECYCLER, MoreMachineFactoryType.RECYCLING)
            .withGui(() -> MoreMachineContainerTypes.RECYCLER)
            .withSound(MekanismSounds.PRECISION_SAWMILL)
            .withEnergyConfig(MoreMachineConfig.usage.recycler, MoreMachineConfig.storage.recycler)
            .with(AttributeSideConfig.ELECTRIC_MACHINE)
            .withComputerSupport("recycler")
            .build();

    // Planting Station
    public static final MoreMachineFactoryMachine<TileEntityPlantingStation> PLANTING_STATION = MoreMachineMachineBuilder
            .createMoreMachineFactoryMachine(() -> MoreMachineTileEntityTypes.PLANTING_STATION, MoreMachineLang.DESCRIPTION_PLANTING_STATION, MoreMachineFactoryType.PLANTING_STATION)
            .withGui(() -> MoreMachineContainerTypes.PLANTING_STATION)
            .withSound(MekanismSounds.ENRICHMENT_CHAMBER)
            .withEnergyConfig(MoreMachineConfig.usage.plantingStation, MoreMachineConfig.storage.plantingStation)
            .with(AttributeUpgradeSupport.DEFAULT_ADVANCED_MACHINE_UPGRADES)
            .with(AttributeSideConfig.ADVANCED_ELECTRIC_MACHINE)
            .withCustomShape(MoreMachineBlockShapes.PLANTING_STATION)
            .with(AttributeHasBounding.ABOVE_ONLY)
            .withComputerSupport("plantingStation")
            .build();

    // CNC Stamper
    public static final MoreMachineFactoryMachine<TileEntityStamper> CNC_STAMPER = MoreMachineMachineBuilder
            .createMoreMachineFactoryMachine(() -> MoreMachineTileEntityTypes.CNC_STAMPER, MoreMachineLang.DESCRIPTION_CNC_STAMPER, MoreMachineFactoryType.CNC_STAMPING)
            .withGui(() -> MoreMachineContainerTypes.CNC_STAMPER)
            .withSound(MekanismSounds.CRUSHER)
            .withEnergyConfig(MoreMachineConfig.usage.cnc_stamper, MoreMachineConfig.storage.cnc_stamper)
            .with(AttributeSideConfig.ELECTRIC_MACHINE)
            .withComputerSupport("cnc_stamper")
            .build();

    // CNC Lathe
    public static final MoreMachineFactoryMachine<TileEntityLathe> CNC_LATHE = MoreMachineMachineBuilder
            .createMoreMachineFactoryMachine(() -> MoreMachineTileEntityTypes.CNC_LATHE, MoreMachineLang.DESCRIPTION_CNC_LATHE, MoreMachineFactoryType.CNC_LATHING)
            .withGui(() -> MoreMachineContainerTypes.CNC_LATHE)
            .withSound(MekanismSounds.OSMIUM_COMPRESSOR)
            .withEnergyConfig(MoreMachineConfig.usage.cnc_lathe, MoreMachineConfig.storage.cnc_lathe)
            .with(AttributeSideConfig.ELECTRIC_MACHINE)
            .withComputerSupport("cnc_lathe")
            .build();

    // CNC Rolling Mill
    public static final MoreMachineFactoryMachine<TileEntityRollingMill> CNC_ROLLING_MILL = MoreMachineMachineBuilder
            .createMoreMachineFactoryMachine(() -> MoreMachineTileEntityTypes.CNC_ROLLING_MILL, MoreMachineLang.DESCRIPTION_CNC_ROLLING_MILL, MoreMachineFactoryType.CNC_ROLLING_MILL)
            .withGui(() -> MoreMachineContainerTypes.CNC_ROLLING_MILL)
            .withSound(MekanismSounds.COMBINER)
            .withEnergyConfig(MoreMachineConfig.usage.cnc_rollingMill, MoreMachineConfig.storage.cnc_rollingMill)
            .with(AttributeSideConfig.ELECTRIC_MACHINE)
            .withComputerSupport("cnc_rolling_mill")
            .build();

    // Replicator
    public static final MoreMachineFactoryMachine<TileEntityReplicator> REPLICATOR = MoreMachineMachineBuilder
            .createMoreMachineFactoryMachine(() -> MoreMachineTileEntityTypes.REPLICATOR, MoreMachineLang.DESCRIPTION_REPLICATOR, MoreMachineFactoryType.REPLICATING)
            .withGui(() -> MoreMachineContainerTypes.REPLICATOR)
            .withEnergyConfig(MoreMachineConfig.usage.itemReplicator, MoreMachineConfig.storage.itemReplicator)
            .withSound(MekanismSounds.PURIFICATION_CHAMBER)
            .with(AttributeSideConfig.ADVANCED_ELECTRIC_MACHINE)
            .withCustomShape(MoreMachineBlockShapes.REPLICATOR)
            .withComputerSupport("itemReplicator")
            .build();

    public static final MoreMachineFactoryMachine<TileEntityFluidReplicator> FLUID_REPLICATOR = MoreMachineMachineBuilder
            .createMoreMachineFactoryMachine(() -> MoreMachineTileEntityTypes.FLUID_REPLICATOR, MoreMachineLang.DESCRIPTION_FLUID_REPLICATOR, MoreMachineFactoryType.REPLICATING)
            .withGui(() -> MoreMachineContainerTypes.FLUID_REPLICATOR)
            .withEnergyConfig(MoreMachineConfig.usage.fluidReplicator, MoreMachineConfig.storage.fluidReplicator)
            .withSound(MekanismSounds.PURIFICATION_CHAMBER)
            .withSideConfig(TransmissionType.FLUID, TransmissionType.CHEMICAL, TransmissionType.ITEM, TransmissionType.ENERGY)
            .withCustomShape(MoreMachineBlockShapes.REPLICATOR)
            .withComputerSupport("fluidReplicator")
            .build();

    public static final MoreMachineFactoryMachine<TileEntityChemicalReplicator> CHEMICAL_REPLICATOR = MoreMachineMachineBuilder
            .createMoreMachineFactoryMachine(() -> MoreMachineTileEntityTypes.CHEMICAL_REPLICATOR, MoreMachineLang.DESCRIPTION_CHEMicAL_REPLICATOR, MoreMachineFactoryType.REPLICATING)
            .withGui(() -> MoreMachineContainerTypes.CHEMIcAL_REPLICATOR)
            .withEnergyConfig(MoreMachineConfig.usage.chemicalReplicator, MoreMachineConfig.storage.chemicalReplicator)
            .withSound(MekanismSounds.PURIFICATION_CHAMBER)
            .withSideConfig(TransmissionType.CHEMICAL, TransmissionType.ITEM, TransmissionType.ENERGY)
            .withCustomShape(MoreMachineBlockShapes.REPLICATOR)
            .withComputerSupport("chemicalReplicator")
            .build();

    static {
        for (FactoryTier tier : MoreMachineUtils.getFactoryTier()) {
            for (MoreMachineFactoryType type : MoreMachineEnumUtils.MM_FACTORY_TYPES) {
                MM_FACTORIES.put(tier, type, MoreMachineFactoryBuilder.createMoreMachineFactory(() -> MoreMachineTileEntityTypes.getMoreMachineFactoryTile(tier, type), type, tier).build());
            }
        }
    }

    // Ambient Gas Collector
    public static final Machine<TileEntityAmbientGasCollector> AMBIENT_GAS_COLLECTOR = MachineBuilder
            .createMachine(() -> MoreMachineTileEntityTypes.AMBIENT_GAS_COLLECTOR, MoreMachineLang.DESCRIPTION_AMBIENT_GAS_COLLECTOR)
            .withGui(() -> MoreMachineContainerTypes.AMBIENT_GAS_COLLECTOR)
            .withEnergyConfig(MoreMachineConfig.usage.ambientGasCollector, MoreMachineConfig.storage.ambientGasCollector)
            .withSupportedUpgrades(Upgrade.SPEED, Upgrade.ENERGY)
            .withCustomShape(MoreMachineBlockShapes.AMBIENT_GAS_COLLECTOR)
            .withComputerSupport("ambientGasCollector")
            .replace(Attributes.ACTIVE)
            .build();

    // Wireless Charging Station
    public static final Machine<TileEntityWirelessChargingStation> WIRELESS_CHARGING_STATION = MachineBuilder
            .createMachine(() -> MoreMachineTileEntityTypes.WIRELESS_CHARGING_STATION, MoreMachineLang.DESCRIPTION_WIRELESS_CHARGING_STATION)
            .withGui(() -> MoreMachineContainerTypes.WIRELESS_CHARGING_STATION)
            .withEnergyConfig(MoreMachineConfig.storage.wirelessChargingStation)
            .withSideConfig(TransmissionType.ITEM, TransmissionType.ENERGY)
            .withCustomShape(MoreMachineBlockShapes.WIRELESS_CHARGING_STATION)
            .with(AttributeCustomSelectionBox.JSON)
            .without(AttributeUpgradeSupport.class)
            .with(MoreMachineBounding.VERTICAL_THREE_BLOCK)
            .withComputerSupport("wirelessChargingStation")
            .replace(Attributes.ACTIVE)
            .build();

    // Wireless Transmission Station
    public static final Machine<TileEntityWirelessTransmissionStation> WIRELESS_TRANSMISSION_STATION = MachineBuilder
            .createMachine(() -> MoreMachineTileEntityTypes.WIRELESS_TRANSMISSION_STATION, MoreMachineLang.DESCRIPTION_WIRELESS_TRANSMISSION_STATION)
            .withGui(() -> MoreMachineContainerTypes.WIRELESS_TRANSMISSION_STATION)
            .withEnergyConfig(MoreMachineConfig.storage.wirelessTransmitterStation)
            .withSideConfig(TransmissionType.ITEM, TransmissionType.CHEMICAL, TransmissionType.FLUID, TransmissionType.ENERGY, TransmissionType.HEAT)
            .withCustomShape(MoreMachineBlockShapes.WIRELESS_TRANSMISSION_STATION)
            .with(AttributeCustomSelectionBox.JSON)
            .without(AttributeUpgradeSupport.class)
            .with(MoreMachineBounding.VERTICAL_THREE_BLOCK)
            .withComputerSupport("wirelessTransmissionStation")
            .replace(Attributes.ACTIVE)
            .build();

    // Author Doll
    public static final BlockTypeTile<TileEntityAuthorDoll> AUTHOR_DOLL = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> MoreMachineTileEntityTypes.AUTHOR_DOLL, MoreMachineLang.AUTHOR_DOLL)
            .with(new AttributeStateFacing(BlockStateProperties.HORIZONTAL_FACING))
            .withCustomShape(MoreMachineBlockShapes.AUTHOR_DOLL)
            .with(AttributeCustomSelectionBox.JSON)
            .build();

    // Modeler Doll
    public static final BlockTypeTile<TileEntityModelerDoll> MODELER_DOLL = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> MoreMachineTileEntityTypes.MODELER_DOLL, MoreMachineLang.MODELER_DOLL)
            .with(new AttributeStateFacing(BlockStateProperties.HORIZONTAL_FACING))
            .withCustomShape(MoreMachineBlockShapes.AUTHOR_DOLL)
            .with(AttributeCustomSelectionBox.JSON)
            .build();

    public static MoreMachineFactory<?> getMoreMachineFactory(FactoryTier tier, MoreMachineFactoryType type) {
        return MM_FACTORIES.get(tier, type);
    }
}
