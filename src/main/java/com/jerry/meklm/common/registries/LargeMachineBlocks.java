package com.jerry.meklm.common.registries;

import com.jerry.mekmm.Mekmm;

import mekanism.api.tier.ITier;
import mekanism.common.block.attribute.AttributeTier;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.content.blocktype.BlockType;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

import com.jerry.meklm.common.item.block.ItemBlockMaxChemicalTank;
import com.jerry.meklm.common.item.block.ItemBlockMidChemicalTank;
import com.jerry.meklm.common.tile.TileEntityMaxChemicalTank;
import com.jerry.meklm.common.tile.TileEntityMidChemicalTank;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class LargeMachineBlocks {

    private LargeMachineBlocks() {}

    public static final BlockDeferredRegister LM_BLOCKS = new BlockDeferredRegister(Mekmm.MOD_ID);

    public static final BlockRegistryObject<BlockTileModel<TileEntityMidChemicalTank, Machine<TileEntityMidChemicalTank>>, ItemBlockMidChemicalTank> BASIC_MID_CHEMICAL_TANK = registerMidChemicalTank(LargeMachineBlockTypes.BASIC_MID_CHEMICAL_TANK);
    public static final BlockRegistryObject<BlockTileModel<TileEntityMidChemicalTank, Machine<TileEntityMidChemicalTank>>, ItemBlockMidChemicalTank> ADVANCED_MID_CHEMICAL_TANK = registerMidChemicalTank(LargeMachineBlockTypes.ADVANCED_MID_CHEMICAL_TANK);
    public static final BlockRegistryObject<BlockTileModel<TileEntityMidChemicalTank, Machine<TileEntityMidChemicalTank>>, ItemBlockMidChemicalTank> ELITE_MID_CHEMICAL_TANK = registerMidChemicalTank(LargeMachineBlockTypes.ELITE_MID_CHEMICAL_TANK);
    public static final BlockRegistryObject<BlockTileModel<TileEntityMidChemicalTank, Machine<TileEntityMidChemicalTank>>, ItemBlockMidChemicalTank> ULTIMATE_MID_CHEMICAL_TANK = registerMidChemicalTank(LargeMachineBlockTypes.ULTIMATE_MID_CHEMICAL_TANK);

    public static final BlockRegistryObject<BlockTileModel<TileEntityMaxChemicalTank, Machine<TileEntityMaxChemicalTank>>, ItemBlockMaxChemicalTank> BASIC_MAX_CHEMICAL_TANK = registerMaxChemicalTank(LargeMachineBlockTypes.BASIC_MAX_CHEMICAL_TANK);
    public static final BlockRegistryObject<BlockTileModel<TileEntityMaxChemicalTank, Machine<TileEntityMaxChemicalTank>>, ItemBlockMaxChemicalTank> ADVANCED_MAX_CHEMICAL_TANK = registerMaxChemicalTank(LargeMachineBlockTypes.ADVANCED_MAX_CHEMICAL_TANK);
    public static final BlockRegistryObject<BlockTileModel<TileEntityMaxChemicalTank, Machine<TileEntityMaxChemicalTank>>, ItemBlockMaxChemicalTank> ELITE_MAX_CHEMICAL_TANK = registerMaxChemicalTank(LargeMachineBlockTypes.ELITE_MAX_CHEMICAL_TANK);
    public static final BlockRegistryObject<BlockTileModel<TileEntityMaxChemicalTank, Machine<TileEntityMaxChemicalTank>>, ItemBlockMaxChemicalTank> ULTIMATE_MAX_CHEMICAL_TANK = registerMaxChemicalTank(LargeMachineBlockTypes.ULTIMATE_MAX_CHEMICAL_TANK);

    private static BlockRegistryObject<BlockTileModel<TileEntityMidChemicalTank, Machine<TileEntityMidChemicalTank>>, ItemBlockMidChemicalTank> registerMidChemicalTank(
                                                                                                                                                                        Machine<TileEntityMidChemicalTank> type) {
        return registerTieredBlock(type, "_mid_chemical_tank", color -> new BlockTileModel<>(type, properties -> properties.mapColor(color)), ItemBlockMidChemicalTank::new);
    }

    private static BlockRegistryObject<BlockTileModel<TileEntityMaxChemicalTank, Machine<TileEntityMaxChemicalTank>>, ItemBlockMaxChemicalTank> registerMaxChemicalTank(
                                                                                                                                                                        Machine<TileEntityMaxChemicalTank> type) {
        return registerTieredBlock(type, "_max_chemical_tank", color -> new BlockTileModel<>(type, properties -> properties.mapColor(color)), ItemBlockMaxChemicalTank::new);
    }

    private static <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerTieredBlock(BlockType type, String suffix,
                                                                                                                      Function<MapColor, ? extends BLOCK> blockSupplier, Function<BLOCK, ITEM> itemCreator) {
        ITier tier = Objects.requireNonNull(type.get(AttributeTier.class)).tier();
        return registerTieredBlock(tier, suffix, () -> blockSupplier.apply(tier.getBaseTier().getMapColor()), itemCreator);
    }

    private static <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerTieredBlock(ITier tier, String suffix,
                                                                                                                      Supplier<? extends BLOCK> blockSupplier, Function<BLOCK, ITEM> itemCreator) {
        return LM_BLOCKS.register(tier.getBaseTier().getLowerName() + suffix, blockSupplier, itemCreator);
    }
}
