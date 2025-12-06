package com.jerry.mekmm.common.registries;

import com.jerry.mekmm.common.MoreMachineLang;
import com.jerry.mekmm.common.config.MoreMachineConfig;
import com.jerry.mekmm.common.content.blocktype.MoreMachineBlockShapes;
import com.jerry.mekmm.common.content.blocktype.MoreMachineFactory;
import com.jerry.mekmm.common.content.blocktype.MoreMachineFactoryType;
import com.jerry.mekmm.common.content.blocktype.MoreMachineMachine;
import com.jerry.mekmm.common.content.blocktype.MoreMachineMachine.MoreMachineFactoryMachine;
import com.jerry.mekmm.common.content.blocktype.MoreMachineMachine.MoreMachineMachineBuilder;
import com.jerry.mekmm.common.tile.TileEntityAuthorDoll;
import com.jerry.mekmm.common.tile.TileEntityModelerDoll;
import com.jerry.mekmm.common.tile.TileEntityWirelessChargingStation;
import com.jerry.mekmm.common.tile.machine.*;
import com.jerry.mekmm.common.util.MoreMachineEnumUtils;

import mekanism.api.Upgrade;
import mekanism.common.block.attribute.AttributeCustomSelectionBox;
import mekanism.common.block.attribute.AttributeStateFacing;
import mekanism.common.block.attribute.AttributeUpgradeSupport;
import mekanism.common.block.attribute.Attributes;
import mekanism.common.content.blocktype.BlockShapes;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.registries.MekanismSounds;
import mekanism.common.tier.FactoryTier;
import mekanism.common.util.EnumUtils;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.EnumSet;

public class MoreMachineBlockTypes {

    private MoreMachineBlockTypes() {}

    private static final Table<FactoryTier, MoreMachineFactoryType, MoreMachineFactory<?>> FACTORIES = HashBasedTable.create();

    // Recycler
    public static final MoreMachineFactoryMachine<TileEntityRecycler> RECYCLER = MoreMachineMachineBuilder
            .createMMFactoryMachine(() -> MoreMachineTileEntityTypes.RECYCLER, MoreMachineLang.DESCRIPTION_RECYCLER, MoreMachineFactoryType.RECYCLING)
            .withGui(() -> MoreMachineContainerTypes.RECYCLER)
            .withSound(MekanismSounds.PRECISION_SAWMILL)
            .withEnergyConfig(MoreMachineConfig.usage.recycler, MoreMachineConfig.storage.recycler)
            .withComputerSupport("recycler")
            .build();
    // Planting Station
    public static final MoreMachineFactoryMachine<TileEntityPlantingStation> PLANTING_STATION = MoreMachineMachineBuilder
            .createMMFactoryMachine(() -> MoreMachineTileEntityTypes.PLANTING_STATION, MoreMachineLang.DESCRIPTION_PLANTING_STATION, MoreMachineFactoryType.PLANTING)
            .withGui(() -> MoreMachineContainerTypes.PLANTING_STATION)
            .withSound(MekanismSounds.PRECISION_SAWMILL)
            .withEnergyConfig(MoreMachineConfig.usage.plantingStation, MoreMachineConfig.storage.plantingStation)
            .withSupportedUpgrades(EnumSet.of(Upgrade.SPEED, Upgrade.ENERGY, Upgrade.MUFFLING, Upgrade.GAS))
            .withBounding((pos, state, builder) -> builder.add(pos.above()))
            .withCustomShape(MoreMachineBlockShapes.PLANTING_STATION)
            .withComputerSupport("plantingStation")
            .build();
    // CNC Stamper
    public static final MoreMachineFactoryMachine<TileEntityStamper> CNC_STAMPER = MoreMachineMachineBuilder
            .createMMFactoryMachine(() -> MoreMachineTileEntityTypes.CNC_STAMPER, MoreMachineLang.DESCRIPTION_CNC_STAMPER, MoreMachineFactoryType.CNC_STAMPING)
            .withGui(() -> MoreMachineContainerTypes.CNC_STAMPER)
            .withSound(MekanismSounds.CRUSHER)
            .withEnergyConfig(MoreMachineConfig.usage.cnc_stamper, MoreMachineConfig.storage.cnc_stamper)
            .withComputerSupport("cnc_stamper")
            .build();
    // CNC Lathe
    public static final MoreMachineFactoryMachine<TileEntityLathe> CNC_LATHE = MoreMachineMachineBuilder
            .createMMFactoryMachine(() -> MoreMachineTileEntityTypes.CNC_LATHE, MoreMachineLang.DESCRIPTION_CNC_LATHE, MoreMachineFactoryType.CNC_LATHING)
            .withGui(() -> MoreMachineContainerTypes.CNC_LATHE)
            .withSound(MekanismSounds.CRUSHER)
            .withEnergyConfig(MoreMachineConfig.usage.cnc_lathe, MoreMachineConfig.storage.cnc_lathe)
            .withComputerSupport("cnc_lathe")
            .build();
    // CNC Rolling Mill
    public static final MoreMachineFactoryMachine<TileEntityRollingMill> CNC_ROLLING_MILL = MoreMachineMachineBuilder
            .createMMFactoryMachine(() -> MoreMachineTileEntityTypes.CNC_ROLLING_MILL, MoreMachineLang.DESCRIPTION_CNC_ROLLING_MILL, MoreMachineFactoryType.CNC_ROLLING_MILL)
            .withGui(() -> MoreMachineContainerTypes.CNC_ROLLING_MILL)
            .withSound(MekanismSounds.CRUSHER)
            .withEnergyConfig(MoreMachineConfig.usage.cnc_rollingMill, MoreMachineConfig.storage.cnc_rollingMill)
            .withComputerSupport("cnc_rolling_mill")
            .build();
    // Replicator
    public static final MoreMachineFactoryMachine<TileEntityReplicator> REPLICATOR = MoreMachineMachineBuilder
            .createMMFactoryMachine(() -> MoreMachineTileEntityTypes.REPLICATOR, MoreMachineLang.DESCRIPTION_REPLICATOR, MoreMachineFactoryType.REPLICATING)
            .withGui(() -> MoreMachineContainerTypes.REPLICATOR)
            .withSound(MekanismSounds.PRECISION_SAWMILL)
            .withEnergyConfig(MoreMachineConfig.usage.itemReplicator, MoreMachineConfig.storage.itemReplicator)
            .withComputerSupport("replicator")
            .build();
    // Fluid Replicator
    public static final MoreMachineFactoryMachine<TileEntityFluidReplicator> FLUID_REPLICATOR = MoreMachineMachineBuilder
            .createMMFactoryMachine(() -> MoreMachineTileEntityTypes.FLUID_REPLICATOR, MoreMachineLang.DESCRIPTION_FLUID_REPLICATOR, MoreMachineFactoryType.REPLICATING)
            .withGui(() -> MoreMachineContainerTypes.FLUID_REPLICATOR)
            .withSound(MekanismSounds.PRECISION_SAWMILL)
            .withEnergyConfig(MoreMachineConfig.usage.fluidReplicator, MoreMachineConfig.storage.fluidReplicator)
            .withComputerSupport("fluidReplicator")
            .build();
    // Ambient Gas Collector
    public static final MoreMachineMachine<TileEntityAmbientGasCollector> AMBIENT_GAS_COLLECTOR = MoreMachineMachineBuilder
            .createMMMachine(() -> MoreMachineTileEntityTypes.AMBIENT_GAS_COLLECTOR, MoreMachineLang.DESCRIPTION_AMBIENT_GAS_COLLECTOR)
            .withGui(() -> MoreMachineContainerTypes.AMBIENT_GAS_COLLECTOR)
            .withEnergyConfig(MoreMachineConfig.usage.ambientGasCollector, MoreMachineConfig.storage.ambientGasCollector)
            .withSupportedUpgrades(EnumSet.of(Upgrade.SPEED, Upgrade.ENERGY))
            .withCustomShape(MoreMachineBlockShapes.AMBIENT_GAS_COLLECTOR)
            .withComputerSupport("ambientGasCollector")
            .replace(Attributes.ACTIVE)
            .build();
    // Wireless Charging Station
    public static final MoreMachineMachine<TileEntityWirelessChargingStation> WIRELESS_CHARGING_STATION = MoreMachineMachineBuilder
            .createMMMachine(() -> MoreMachineTileEntityTypes.WIRELESS_CHARGING_STATION, MoreMachineLang.DESCRIPTION_WIRELESS_CHARGING_STATION)
            .withGui(() -> MoreMachineContainerTypes.WIRELESS_CHARGING_STATION)
            .withEnergyConfig(MoreMachineConfig.storage.wirelessChargingStation)
            .withCustomShape(MoreMachineBlockShapes.WIRELESS_CHARGING_STATION)
            .with(AttributeCustomSelectionBox.JSON)
            .without(AttributeUpgradeSupport.class)
            .withBounding((pos, state, builder) -> {
                for (int i = 0; i < 2; i++) {
                    builder.add(pos.above(i + 1));
                }
            })
            .withComputerSupport("wirelessChargingStation")
            .replace(Attributes.ACTIVE)
            .build();
    // Author Doll
    public static final BlockTypeTile<TileEntityAuthorDoll> AUTHOR_DOLL = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> MoreMachineTileEntityTypes.AUTHOR_DOLL, MoreMachineLang.DESCRIPTION_AUTHOR_DOLL)
            .with(new AttributeStateFacing(BlockStateProperties.HORIZONTAL_FACING))
            .withCustomShape(MoreMachineBlockShapes.AUTHOR_DOLL)
            .with(AttributeCustomSelectionBox.JSON)
            .build();

    // Modeler Doll
    public static final BlockTypeTile<TileEntityModelerDoll> MODELER_DOLL = BlockTypeTile.BlockTileBuilder
            .createBlock(() -> MoreMachineTileEntityTypes.MODELER_DOLL, MoreMachineLang.DESCRIPTION_MODELER_DOLL)
            .with(new AttributeStateFacing(BlockStateProperties.HORIZONTAL_FACING))
            // 玩偶的模型都是一样的
            .withCustomShape(MoreMachineBlockShapes.AUTHOR_DOLL)
            .with(AttributeCustomSelectionBox.JSON)
            .build();

    static {
        for (FactoryTier tier : EnumUtils.FACTORY_TIERS) {
            for (MoreMachineFactoryType type : MoreMachineEnumUtils.MM_FACTORY_TYPES) {
                FACTORIES.put(tier, type, MoreMachineFactory.MoreMachineFactoryBuilder.createMMFactory(() -> MoreMachineTileEntityTypes.getMoreMachineFactoryTile(tier, type), type, tier).build());
            }
        }
    }

    public static MoreMachineFactory<?> getMoreMachineFactory(FactoryTier tier, MoreMachineFactoryType type) {
        return FACTORIES.get(tier, type);
    }
}
