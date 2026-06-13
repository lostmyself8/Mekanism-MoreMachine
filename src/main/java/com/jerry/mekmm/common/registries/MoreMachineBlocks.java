package com.jerry.mekmm.common.registries;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.api.datamaps.IMoreMachineDataMapTypes;
import com.jerry.mekmm.common.attachments.component.MoreMachineAttachedSideConfig;
import com.jerry.mekmm.common.block.BlockAuthorDoll;
import com.jerry.mekmm.common.block.BlockModelerDoll;
import com.jerry.mekmm.common.block.prefab.BlockMoreFactoryMachine;
import com.jerry.mekmm.common.block.prefab.BlockMoreFactoryMachine.BlockMoreMachineFactory;
import com.jerry.mekmm.common.content.blocktype.MoreMachineFactory;
import com.jerry.mekmm.common.content.blocktype.MoreMachineFactoryType;
import com.jerry.mekmm.common.content.blocktype.MoreMachineMachine.MoreMachineFactoryMachine;
import com.jerry.mekmm.common.item.block.*;
import com.jerry.mekmm.common.item.block.machine.ItemBlockMoreMachineFactory;
import com.jerry.mekmm.common.recipe.MoreMachineRecipeType;
import com.jerry.mekmm.common.tile.factory.TileEntityMoreMachineFactory;
import com.jerry.mekmm.common.tile.factory.TileEntityReplicatingFactory;
import com.jerry.mekmm.common.tile.machine.*;
import com.jerry.mekmm.common.tile.machine.TileEntityWirelessChargingStation;
import com.jerry.mekmm.common.tile.machine.TileEntityWirelessTransmissionStation;
import com.jerry.mekmm.common.util.MoreMachineEnumUtils;
import com.jerry.mekmm.common.util.MoreMachineUtils;

import mekanism.api.tier.ITier;
import mekanism.common.block.attribute.AttributeTier;
import mekanism.common.block.prefab.BlockTile;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.component.component.AttachedEjector;
import mekanism.common.component.component.AttachedSideConfig;
import mekanism.common.component.containers.chemical.ChemicalTanksBuilder;
import mekanism.common.component.containers.fluid.FluidTanksBuilder;
import mekanism.common.component.containers.heat.HeatCapacitorsBuilder;
import mekanism.common.component.containers.item.ItemSlotsBuilder;
import mekanism.common.component.containers.type.ContainerType;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.recipe.lookup.cache.SingleInputRecipeCache;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registries.MekanismDataComponents;
import mekanism.common.resource.BlockResourceInfo;
import mekanism.common.tier.FactoryTier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.transfer.item.ItemResource;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class MoreMachineBlocks {

    private MoreMachineBlocks() {}

    public static final BlockDeferredRegister MM_BLOCKS = new BlockDeferredRegister(Mekmm.MOD_ID);

    private static final Table<FactoryTier, MoreMachineFactoryType, BlockRegistryObject<@NotNull BlockMoreMachineFactory<?>, @NotNull ItemBlockMoreMachineFactory>> MM_FACTORIES = HashBasedTable.create();

    static {
        // factories
        for (FactoryTier tier : MoreMachineUtils.getFactoryTier()) {
            for (MoreMachineFactoryType type : MoreMachineEnumUtils.MM_FACTORY_TYPES) {
                MM_FACTORIES.put(tier, type, registerMoreMachineFactory(MoreMachineBlockTypes.getMoreMachineFactory(tier, type)));
            }
        }
    }

    public static final BlockRegistryObject<@NotNull BlockMoreFactoryMachine<TileEntityRecycler, MoreMachineFactoryMachine<TileEntityRecycler>>, @NotNull ItemBlockTooltip<BlockMoreFactoryMachine<TileEntityRecycler, MoreMachineFactoryMachine<TileEntityRecycler>>>> RECYCLER = MM_BLOCKS.register("recycler", properties -> new BlockMoreFactoryMachine<>(MoreMachineBlockTypes.RECYCLER, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor())),
            (block, properties) -> new ItemBlockTooltip<>(block, true, properties
                    .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                    .component(MekanismDataComponents.SIDE_CONFIG, AttachedSideConfig.ELECTRIC_MACHINE)))
            .forItemHolder(holder -> holder.addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                    .addInput(MoreMachineRecipeType.RECYCLING, SingleInputRecipeCache::containsInput)
                    .addOutput()
                    .addEnergy()
                    .build()));

    public static final BlockRegistryObject<@NotNull BlockMoreFactoryMachine<TileEntityPlantingStation, MoreMachineFactoryMachine<TileEntityPlantingStation>>, @NotNull ItemBlockTooltip<BlockMoreFactoryMachine<TileEntityPlantingStation, MoreMachineFactoryMachine<TileEntityPlantingStation>>>> PLANTING_STATION = MM_BLOCKS.register("planting_station", properties -> new BlockMoreFactoryMachine<>(MoreMachineBlockTypes.PLANTING_STATION, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor())),
            (block, properties) -> new ItemBlockTooltip<>(block, true, properties
                    .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                    .component(MekanismDataComponents.SIDE_CONFIG, AttachedSideConfig.ADVANCED_MACHINE_INPUT_ONLY)))
            .forItemHolder(holder -> holder.addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> ChemicalTanksBuilder.builder()
                    .addBasic(TileEntityPlantingStation.MAX_GAS, MoreMachineRecipeType.PLANTING_STATION, InputRecipeCache.ItemChemical::containsInputB)
                    .build()).addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                            .addInput(MoreMachineRecipeType.PLANTING_STATION, InputRecipeCache.ItemChemical::containsInputA)
                            .addChemicalFillOrConvertSlot(1)
                            .addOutput()
                            .addOutput()// Secondary output
                            .addEnergy()
                            .build()));

    public static final BlockRegistryObject<@NotNull BlockMoreFactoryMachine<TileEntityStamper, MoreMachineFactoryMachine<TileEntityStamper>>, @NotNull ItemBlockTooltip<BlockMoreFactoryMachine<TileEntityStamper, MoreMachineFactoryMachine<TileEntityStamper>>>> CNC_STAMPER = MM_BLOCKS.register("cnc_stamper", properties -> new BlockMoreFactoryMachine<>(MoreMachineBlockTypes.CNC_STAMPER, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor())),
            (block, properties) -> new ItemBlockTooltip<>(block, true, properties
                    .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                    .component(MekanismDataComponents.SIDE_CONFIG, AttachedSideConfig.EXTRA_MACHINE)))
            .forItemHolder(holder -> holder.addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                    .addInput(MoreMachineRecipeType.STAMPING, InputRecipeCache.DoubleItem::containsInputA)
                    .addInput(MoreMachineRecipeType.STAMPING, InputRecipeCache.DoubleItem::containsInputB)
                    .addOutput()
                    .addEnergy()
                    .build()));

    public static final BlockRegistryObject<@NotNull BlockMoreFactoryMachine<TileEntityLathe, MoreMachineFactoryMachine<TileEntityLathe>>, @NotNull ItemBlockTooltip<BlockMoreFactoryMachine<TileEntityLathe, MoreMachineFactoryMachine<TileEntityLathe>>>> CNC_LATHE = MM_BLOCKS.register("cnc_lathe", properties -> new BlockMoreFactoryMachine<>(MoreMachineBlockTypes.CNC_LATHE, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor())),
            (block, properties) -> new ItemBlockTooltip<>(block, true, properties
                    .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                    .component(MekanismDataComponents.SIDE_CONFIG, AttachedSideConfig.ELECTRIC_MACHINE)))
            .forItemHolder(holder -> holder.addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                    .addInput(MoreMachineRecipeType.LATHING, SingleInputRecipeCache::containsInput)
                    .addOutput()
                    .addEnergy()
                    .build()));

    public static final BlockRegistryObject<@NotNull BlockMoreFactoryMachine<TileEntityRollingMill, MoreMachineFactoryMachine<TileEntityRollingMill>>, @NotNull ItemBlockTooltip<BlockMoreFactoryMachine<TileEntityRollingMill, MoreMachineFactoryMachine<TileEntityRollingMill>>>> CNC_ROLLING_MILL = MM_BLOCKS.register("cnc_rolling_mill", properties -> new BlockMoreFactoryMachine<>(MoreMachineBlockTypes.CNC_ROLLING_MILL, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor())),
            (block, properties) -> new ItemBlockTooltip<>(block, true, properties
                    .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                    .component(MekanismDataComponents.SIDE_CONFIG, AttachedSideConfig.ELECTRIC_MACHINE)))
            .forItemHolder(holder -> holder.addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                    .addInput(MoreMachineRecipeType.ROLLING_MILL, SingleInputRecipeCache::containsInput)
                    .addOutput()
                    .addEnergy()
                    .build()));

    public static final BlockRegistryObject<@NotNull BlockMoreFactoryMachine<TileEntityReplicator, MoreMachineFactoryMachine<TileEntityReplicator>>, @NotNull ItemBlockTooltip<BlockMoreFactoryMachine<TileEntityReplicator, MoreMachineFactoryMachine<TileEntityReplicator>>>> REPLICATOR = MM_BLOCKS.register("replicator", properties -> new BlockMoreFactoryMachine<>(MoreMachineBlockTypes.REPLICATOR, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor())),
            (block, properties) -> new ItemBlockTooltip<>(block, true, properties
                    .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                    .component(MekanismDataComponents.SIDE_CONFIG, AttachedSideConfig.ADVANCED_MACHINE_INPUT_ONLY)))
            .forItemHolder(holder -> holder
                    .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> ChemicalTanksBuilder.builder()
                            .addBasic(TileEntityReplicator.MAX_GAS, TileEntityReplicator::isValidChemicalInput)
                            .build())
                    .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                            .addInput(TileEntityReplicator::isValidItemInput)
                            .addOutput()
                            .addChemicalFillOrConvertSlot(0)
                            .addEnergy()
                            .build()));

    public static final BlockRegistryObject<@NotNull BlockMoreFactoryMachine<TileEntityFluidReplicator, MoreMachineFactoryMachine<TileEntityFluidReplicator>>, @NotNull ItemBlockTooltip<BlockMoreFactoryMachine<TileEntityFluidReplicator, MoreMachineFactoryMachine<TileEntityFluidReplicator>>>> FLUID_REPLICATOR = MM_BLOCKS.register("fluid_replicator", properties -> new BlockMoreFactoryMachine<>(MoreMachineBlockTypes.FLUID_REPLICATOR, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor())),
            (block, properties) -> new ItemBlockTooltip<>(block, true, properties
                    .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                    .component(MekanismDataComponents.SIDE_CONFIG, MoreMachineAttachedSideConfig.FLUID_REPLICATOR)))
            .forItemHolder(holder -> holder
                    .addAttachmentOnlyContainers(ContainerType.FLUID, () -> FluidTanksBuilder.builder()
                            .addBasic(TileEntityFluidReplicator.MAX_FLUID / 2, TileEntityFluidReplicator::isValidFluidInput)
                            .addBasic(TileEntityFluidReplicator.MAX_FLUID)
                            .build())
                    .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> ChemicalTanksBuilder.builder()
                            .addBasic(() -> TileEntityReplicator.MAX_GAS, TileEntityFluidReplicator::isValidChemicalInput)
                            .build())
                    .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                            .addFluidFillSlot(0)// 右侧上槽
                            .addOutput()// 右侧下槽
                            .addFluidDrainSlot(2)// 左侧液体槽
                            .addOutput()// 右侧液体槽
                            .addChemicalFillOrConvertSlot(4)// uu物质槽
                            .addEnergy()
                            .build()));

    public static final BlockRegistryObject<@NotNull BlockMoreFactoryMachine<TileEntityChemicalReplicator, MoreMachineFactoryMachine<TileEntityChemicalReplicator>>, @NotNull ItemBlockTooltip<BlockMoreFactoryMachine<TileEntityChemicalReplicator, MoreMachineFactoryMachine<TileEntityChemicalReplicator>>>> CHEMICAL_REPLICATOR = MM_BLOCKS.register("chemical_replicator", properties -> new BlockMoreFactoryMachine<>(MoreMachineBlockTypes.CHEMICAL_REPLICATOR, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor())),
            (block, properties) -> new ItemBlockTooltip<>(block, true, properties
                    .component(MekanismDataComponents.EJECTOR, AttachedEjector.DEFAULT)
                    .component(MekanismDataComponents.SIDE_CONFIG, MoreMachineAttachedSideConfig.CHEMICAL_REPLICATOR)))
            .forItemHolder(holder -> holder
                    .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> ChemicalTanksBuilder.builder()
                            .addBasic(() -> TileEntityChemicalReplicator.MAX_GAS, TileEntityChemicalReplicator::isValidChemicalInput)
                            .addBasic(() -> TileEntityChemicalReplicator.MAX_GAS, TileEntityChemicalReplicator::isValidInputChemical)
                            .addBasic(() -> TileEntityChemicalReplicator.MAX_GAS)
                            .build())
                    .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                            .addChemicalFillOrConvertSlot(0)
                            .addChemicalFillOrConvertSlot(1)
                            .addOutput()
                            .addEnergy()
                            .build()));

    private static <TILE extends TileEntityMoreMachineFactory<?>> BlockRegistryObject<@NotNull BlockMoreMachineFactory<?>, @NotNull ItemBlockMoreMachineFactory> registerMoreMachineFactory(MoreMachineFactory<TILE> type) {
        FactoryTier tier = (FactoryTier) Objects.requireNonNull(type.get(AttributeTier.class)).tier();
        BlockRegistryObject<@NotNull BlockMoreMachineFactory<?>, @NotNull ItemBlockMoreMachineFactory> factory = registerTieredBlock(tier, "_" + type.getMoreMachineFactoryType().getRegistryNameComponent() + "_factory", properties -> new BlockMoreMachineFactory<>(type, properties), ItemBlockMoreMachineFactory::new);
        factory.forItemHolder(holder -> {
            int processes = tier.processes;
            Predicate<ItemResource> recipeInputPredicate = switch (type.getMoreMachineFactoryType()) {
                case RECYCLING -> s -> MoreMachineRecipeType.RECYCLING.getInputCache().containsInput(null, s);
                case PLANTING_STATION -> s -> MoreMachineRecipeType.PLANTING_STATION.getInputCache().containsInputA(null, s);
                case CNC_STAMPING -> s -> MoreMachineRecipeType.STAMPING.getInputCache().containsInputA(null, s);
                case CNC_LATHING -> s -> MoreMachineRecipeType.LATHING.getInputCache().containsInput(null, s);
                case CNC_ROLLING_MILL -> s -> MoreMachineRecipeType.ROLLING_MILL.getInputCache().containsInput(null, s);
                case REPLICATING -> s -> IMoreMachineDataMapTypes.INSTANCE.getItemReplicatorRecipe(s.typeHolder()) != null;
            };
            switch (type.getMoreMachineFactoryType()) {
                case CNC_STAMPING -> holder.addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                        .addBasicFactorySlots(processes, recipeInputPredicate)
                        .addInput(MekanismRecipeType.COMBINING, InputRecipeCache.DoubleItem::containsInputB)
                        .addEnergy()
                        .build());
                case CNC_LATHING, CNC_ROLLING_MILL, RECYCLING -> holder.addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                        .addBasicFactorySlots(processes, recipeInputPredicate)
                        .addEnergy()
                        .build());
                case PLANTING_STATION -> holder
                        .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> ChemicalTanksBuilder.builder()
                                .addBasic(TileEntityPlantingStation.MAX_GAS * processes, switch (type.getMoreMachineFactoryType()) {
                                    case PLANTING_STATION -> MoreMachineRecipeType.PLANTING_STATION;
                                    default -> throw new IllegalStateException("Factory type doesn't have a known gas recipe.");
                                }, InputRecipeCache.ItemChemical::containsInputB)
                                .build())
                        .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                                .addBasicFactorySlots(processes, recipeInputPredicate, true)
                                .addChemicalFillOrConvertSlot(1)
                                .addEnergy()
                                .build());
                case REPLICATING -> holder.addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> ChemicalTanksBuilder.builder()
                        .addBasic(TileEntityReplicatingFactory.MAX_GAS * processes, TileEntityReplicatingFactory::isValidChemicalInput)
                        .build()).addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                                .addBasicFactorySlots(processes, recipeInputPredicate)
                                .addChemicalFillOrConvertSlot(0)
                                .addEnergy()
                                .build());
            }
        });
        return factory;
    }

    public static final BlockRegistryObject<@NotNull BlockTileModel<TileEntityAmbientGasCollector, Machine<TileEntityAmbientGasCollector>>, @NotNull ItemBlockTooltip<BlockTileModel<TileEntityAmbientGasCollector, Machine<TileEntityAmbientGasCollector>>>> AMBIENT_GAS_COLLECTOR = MM_BLOCKS.registerDetails("ambient_gas_collector", properties -> new BlockTileModel<>(MoreMachineBlockTypes.AMBIENT_GAS_COLLECTOR, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor())))
            .forItemHolder(holder -> holder
                    .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> ChemicalTanksBuilder.builder()
                            .addBasic(TileEntityAmbientGasCollector.MAX_CHEMICAL)
                            .build())
                    .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                            .addChemicalDrainSlot(0)
                            .addEnergy()
                            .build()));

    public static final BlockRegistryObject<@NotNull BlockTileModel<TileEntityWirelessChargingStation, Machine<TileEntityWirelessChargingStation>>, @NotNull ItemBlockWirelessChargingStation> WIRELESS_CHARGING_STATION = MM_BLOCKS.register("wireless_charging_station", properties -> new BlockTileModel<>(MoreMachineBlockTypes.WIRELESS_CHARGING_STATION, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor())), ItemBlockWirelessChargingStation::new)
            .forItemHolder(holder -> holder.addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                    .addEnergy()
                    .addDrainEnergy()
                    .build()));

    public static final BlockRegistryObject<@NotNull BlockTileModel<TileEntityWirelessTransmissionStation, Machine<TileEntityWirelessTransmissionStation>>, @NotNull ItemBlockWirelessTransmissionStation> WIRELESS_TRANSMISSION_STATION = MM_BLOCKS.register("wireless_transmission_station", properties -> new BlockTileModel<>(MoreMachineBlockTypes.WIRELESS_TRANSMISSION_STATION, BlockTile.defaultProperties(properties).mapColor(BlockResourceInfo.STEEL.getMapColor())), ItemBlockWirelessTransmissionStation::new)
            .forItemHolder(holder -> holder
                    .addAttachmentOnlyContainers(ContainerType.FLUID, () -> FluidTanksBuilder.builder()
                            .addBasic(TileEntityWirelessTransmissionStation.MAX_FLUID)
                            .build())
                    .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> ChemicalTanksBuilder.builder()
                            .addBasic(TileEntityWirelessTransmissionStation.MAX_CHEMICAL)
                            .build())
                    .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                            .addEnergy()
                            .addDrainEnergy()
                            .build())
                    .addAttachmentOnlyContainers(ContainerType.HEAT, () -> HeatCapacitorsBuilder.builder()
                            .addBasic(TileEntityWirelessTransmissionStation.HEAT_CAPACITY, TileEntityWirelessTransmissionStation.INVERSE_CONDUCTION_COEFFICIENT, TileEntityWirelessTransmissionStation.INVERSE_INSULATION_COEFFICIENT)
                            .build()));

    public static final BlockRegistryObject<@NotNull BlockAuthorDoll, @NotNull ItemBlockAuthorDoll> AUTHOR_DOLL = MM_BLOCKS.register("author_doll",
            properties -> new BlockAuthorDoll(MoreMachineBlockTypes.AUTHOR_DOLL, BlockTile.defaultProperties(properties).sound(SoundType.WOOL).mapColor(BlockResourceInfo.STEEL.getMapColor())), ItemBlockAuthorDoll::new);

    public static final BlockRegistryObject<@NotNull BlockModelerDoll, @NotNull ItemBlockModelerDoll> MODELER_DOLL = MM_BLOCKS.register("modeler_doll",
            properties -> new BlockModelerDoll(MoreMachineBlockTypes.MODELER_DOLL, BlockTile.defaultProperties(properties).sound(SoundType.WOOL).mapColor(BlockResourceInfo.STEEL.getMapColor())), ItemBlockModelerDoll::new);

    private static <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerTieredBlock(ITier tier, String suffix,
                                                                                                                      Function<BlockBehaviour.Properties, ? extends BLOCK> blockSupplier, BiFunction<BLOCK, Item.Properties, ITEM> itemCreator) {
        return MM_BLOCKS.register(tier.getBaseTier().getLowerName() + suffix, blockSupplier, itemCreator);
    }

    /**
     * Retrieves a Factory with a defined tier and recipe type.
     *
     * @param tier - tier to add to the Factory
     * @param type - recipe type to add to the Factory
     * @return factory with defined tier and recipe type
     */
    public static BlockRegistryObject<@NotNull BlockMoreMachineFactory<?>, @NotNull ItemBlockMoreMachineFactory> getMoreMachineFactory(@NotNull FactoryTier tier, @NotNull MoreMachineFactoryType type) {
        return MM_FACTORIES.get(tier, type);
    }

    @SuppressWarnings("unchecked")
    public static BlockRegistryObject<@NotNull BlockMoreMachineFactory<?>, @NotNull ItemBlockMoreMachineFactory>[] getMoreMachineFactoryBlocks() {
        return MM_FACTORIES.values().toArray(new BlockRegistryObject[0]);
    }
}
