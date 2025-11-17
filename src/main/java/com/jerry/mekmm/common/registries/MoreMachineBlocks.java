package com.jerry.mekmm.common.registries;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.block.BlockDoll;
import com.jerry.mekmm.common.block.prefab.BlockMoreMachineFactoryMachine;
import com.jerry.mekmm.common.content.blocktype.MoreMachineFactory;
import com.jerry.mekmm.common.content.blocktype.MoreMachineFactoryType;
import com.jerry.mekmm.common.content.blocktype.MoreMachineMachine;
import com.jerry.mekmm.common.content.blocktype.MoreMachineMachine.MoreMachineFactoryMachine;
import com.jerry.mekmm.common.item.block.ItemBlockDoll;
import com.jerry.mekmm.common.item.block.machine.ItemBlockMoreMachineFactory;
import com.jerry.mekmm.common.tile.factory.TileEntityMoreMachineFactory;
import com.jerry.mekmm.common.tile.machine.*;
import com.jerry.mekmm.common.util.MoreMachineEnumUtils;

import mekanism.api.tier.ITier;
import mekanism.common.block.attribute.AttributeTier;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.content.blocktype.BlockType;
import mekanism.common.item.block.machine.ItemBlockMachine;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.resource.BlockResourceInfo;
import mekanism.common.tier.FactoryTier;
import mekanism.common.util.EnumUtils;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public class MoreMachineBlocks {

    private MoreMachineBlocks() {}

    public static final BlockDeferredRegister MM_BLOCKS = new BlockDeferredRegister(Mekmm.MOD_ID);

    private static final Table<FactoryTier, MoreMachineFactoryType, BlockRegistryObject<BlockMoreMachineFactoryMachine.BlockMoreMachineFactory<?>, ItemBlockMoreMachineFactory>> FACTORIES = HashBasedTable.create();

    static {
        // factories
        for (FactoryTier tier : EnumUtils.FACTORY_TIERS) {
            for (MoreMachineFactoryType type : MoreMachineEnumUtils.MM_FACTORY_TYPES) {
                FACTORIES.put(tier, type, registerFactory(MoreMachineBlockTypes.getMoreMachineFactory(tier, type)));
            }
        }
    }

    public static final BlockRegistryObject<BlockMoreMachineFactoryMachine<TileEntityRecycler, MoreMachineFactoryMachine<TileEntityRecycler>>, ItemBlockMachine> RECYCLER = MM_BLOCKS.register("recycler", () -> new BlockMoreMachineFactoryMachine<>(MoreMachineBlockTypes.RECYCLER, properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor())), ItemBlockMachine::new);
    public static final BlockRegistryObject<BlockMoreMachineFactoryMachine<TileEntityPlantingStation, MoreMachineFactoryMachine<TileEntityPlantingStation>>, ItemBlockMachine> PLANTING_STATION = MM_BLOCKS.register("planting_station", () -> new BlockMoreMachineFactoryMachine<>(MoreMachineBlockTypes.PLANTING_STATION, properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor())), ItemBlockMachine::new);

    public static final BlockRegistryObject<BlockMoreMachineFactoryMachine<TileEntityStamper, MoreMachineFactoryMachine<TileEntityStamper>>, ItemBlockMachine> CNC_STAMPER = MM_BLOCKS.register("cnc_stamper", () -> new BlockMoreMachineFactoryMachine<>(MoreMachineBlockTypes.CNC_STAMPER, properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor())), ItemBlockMachine::new);
    public static final BlockRegistryObject<BlockMoreMachineFactoryMachine<TileEntityLathe, MoreMachineFactoryMachine<TileEntityLathe>>, ItemBlockMachine> CNC_LATHE = MM_BLOCKS.register("cnc_lathe", () -> new BlockMoreMachineFactoryMachine<>(MoreMachineBlockTypes.CNC_LATHE, properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor())), ItemBlockMachine::new);
    public static final BlockRegistryObject<BlockMoreMachineFactoryMachine<TileEntityRollingMill, MoreMachineFactoryMachine<TileEntityRollingMill>>, ItemBlockMachine> CNC_ROLLING_MILL = MM_BLOCKS.register("cnc_rolling_mill", () -> new BlockMoreMachineFactoryMachine<>(MoreMachineBlockTypes.CNC_ROLLING_MILL, properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor())), ItemBlockMachine::new);

    public static final BlockRegistryObject<BlockMoreMachineFactoryMachine<TileEntityReplicator, MoreMachineFactoryMachine<TileEntityReplicator>>, ItemBlockMachine> REPLICATOR = MM_BLOCKS.register("replicator", () -> new BlockMoreMachineFactoryMachine<>(MoreMachineBlockTypes.REPLICATOR, properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor())), ItemBlockMachine::new);
    public static final BlockRegistryObject<BlockMoreMachineFactoryMachine<TileEntityFluidReplicator, MoreMachineFactoryMachine<TileEntityFluidReplicator>>, ItemBlockMachine> FLUID_REPLICATOR = MM_BLOCKS.register("fluid_replicator", () -> new BlockMoreMachineFactoryMachine<>(MoreMachineBlockTypes.FLUID_REPLICATOR, properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor())), ItemBlockMachine::new);

    public static final BlockRegistryObject<BlockTileModel<TileEntityAmbientGasCollector, MoreMachineMachine<TileEntityAmbientGasCollector>>, ItemBlockMachine> AMBIENT_GAS_COLLECTOR = MM_BLOCKS.register("ambient_gas_collector", () -> new BlockTileModel<>(MoreMachineBlockTypes.AMBIENT_GAS_COLLECTOR, properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor())), ItemBlockMachine::new);

    public static final BlockRegistryObject<BlockDoll, ItemBlockDoll> AUTHOR_DOLL = MM_BLOCKS.register("author_doll",
            () -> new BlockDoll(MoreMachineBlockTypes.AUTHOR_DOLL, BlockBehaviour.Properties.of().sound(SoundType.WOOL).destroyTime(0).strength(0)), ItemBlockDoll::new);

    private static <TILE extends TileEntityMoreMachineFactory<?>> BlockRegistryObject<BlockMoreMachineFactoryMachine.BlockMoreMachineFactory<?>, ItemBlockMoreMachineFactory> registerFactory(MoreMachineFactory<TILE> type) {
        return registerTieredBlock(type, "_" + type.getMMFactoryType().getRegistryNameComponent() + "_factory", () -> new BlockMoreMachineFactoryMachine.BlockMoreMachineFactory<>(type), ItemBlockMoreMachineFactory::new);
    }

    private static <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerTieredBlock(BlockType type, String suffix,
                                                                                                                      Supplier<? extends BLOCK> blockSupplier, Function<BLOCK, ITEM> itemCreator) {
        return registerTieredBlock(type.get(AttributeTier.class).tier(), suffix, blockSupplier, itemCreator);
    }

    private static <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerTieredBlock(ITier tier, String suffix,
                                                                                                                      Supplier<? extends BLOCK> blockSupplier, Function<BLOCK, ITEM> itemCreator) {
        return MM_BLOCKS.register(tier.getBaseTier().getLowerName() + suffix, blockSupplier, itemCreator);
    }

    /**
     * Retrieves a Factory with a defined tier and recipe type.
     *
     * @param tier - tier to add to the Factory
     * @param type - recipe type to add to the Factory
     * @return factory with defined tier and recipe type
     */
    public static BlockRegistryObject<BlockMoreMachineFactoryMachine.BlockMoreMachineFactory<?>, ItemBlockMoreMachineFactory> getMoreMachineFactory(@NotNull FactoryTier tier, @NotNull MoreMachineFactoryType type) {
        return FACTORIES.get(tier, type);
    }

    @SuppressWarnings("unchecked")
    public static BlockRegistryObject<BlockMoreMachineFactoryMachine.BlockMoreMachineFactory<?>, ItemBlockMoreMachineFactory>[] getMoreMachineFactoryBlocks() {
        return FACTORIES.values().toArray(new BlockRegistryObject[0]);
    }
}
