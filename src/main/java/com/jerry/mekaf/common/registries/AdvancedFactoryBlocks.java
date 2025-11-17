package com.jerry.mekaf.common.registries;

import com.jerry.mekaf.common.block.prefab.BlockAdvancedFactoryMachine.BlockAdvancedFactory;
import com.jerry.mekaf.common.content.blocktype.AdvancedFactory;
import com.jerry.mekaf.common.content.blocktype.AdvancedFactoryType;
import com.jerry.mekaf.common.item.ItemBlockAdvancedFactory;
import com.jerry.mekaf.common.tile.base.TileEntityAdvancedFactoryBase;

import com.jerry.mekmm.Mekmm;
import com.jerry.mekmm.common.util.MoreMachineEnumUtils;

import mekanism.api.tier.ITier;
import mekanism.common.block.attribute.AttributeTier;
import mekanism.common.content.blocktype.BlockType;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.tier.FactoryTier;
import mekanism.common.util.EnumUtils;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public class AdvancedFactoryBlocks {

    private AdvancedFactoryBlocks() {}

    public static final BlockDeferredRegister AF_BLOCKS = new BlockDeferredRegister(Mekmm.MOD_ID);

    private static final Table<FactoryTier, AdvancedFactoryType, BlockRegistryObject<BlockAdvancedFactory<?>, ItemBlockAdvancedFactory>> FACTORIES = HashBasedTable.create();

    static {
        // factories
        for (FactoryTier tier : EnumUtils.FACTORY_TIERS) {
            for (AdvancedFactoryType type : MoreMachineEnumUtils.ADVANCED_FACTORY_TYPES) {
                FACTORIES.put(tier, type, registerFactory(AdvancedFactoryBlockTypes.getAdvancedFactory(tier, type)));
            }
        }
    }

    private static <TILE extends TileEntityAdvancedFactoryBase<?>> BlockRegistryObject<BlockAdvancedFactory<?>, ItemBlockAdvancedFactory> registerFactory(AdvancedFactory<TILE> type) {
        return registerTieredBlock(type, "_" + type.getAdvancedFactoryType().getRegistryNameComponent() + "_factory", () -> new BlockAdvancedFactory<>(type), ItemBlockAdvancedFactory::new);
    }

    private static <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerTieredBlock(BlockType type, String suffix,
                                                                                                                      Supplier<? extends BLOCK> blockSupplier, Function<BLOCK, ITEM> itemCreator) {
        return registerTieredBlock(type.get(AttributeTier.class).tier(), suffix, blockSupplier, itemCreator);
    }

    private static <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerTieredBlock(ITier tier, String suffix,
                                                                                                                      Supplier<? extends BLOCK> blockSupplier, Function<BLOCK, ITEM> itemCreator) {
        return AF_BLOCKS.register(tier.getBaseTier().getLowerName() + suffix, blockSupplier, itemCreator);
    }

    /**
     * Retrieves a Factory with a defined tier and recipe type.
     *
     * @param tier - tier to add to the Factory
     * @param type - recipe type to add to the Factory
     *
     * @return factory with defined tier and recipe type
     */
    public static BlockRegistryObject<BlockAdvancedFactory<?>, ItemBlockAdvancedFactory> getAdvancedFactory(@NotNull FactoryTier tier, @NotNull AdvancedFactoryType type) {
        return FACTORIES.get(tier, type);
    }

    @SuppressWarnings("unchecked")
    public static BlockRegistryObject<BlockAdvancedFactory<?>, ItemBlockAdvancedFactory>[] getAdvancedFactoryBlocks() {
        return FACTORIES.values().toArray(new BlockRegistryObject[0]);
    }
}
