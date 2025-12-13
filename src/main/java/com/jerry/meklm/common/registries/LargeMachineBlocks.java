package com.jerry.meklm.common.registries;

import com.jerry.meklm.common.attachments.containers.chemical.ComponentBackedLargeChemicalTankTank;
import com.jerry.meklm.common.item.block.ItemBlockMaxChemicalTank;
import com.jerry.meklm.common.item.block.ItemBlockMidChemicalTank;
import com.jerry.meklm.common.tile.TileEntityMaxChemicalTank;
import com.jerry.meklm.common.tile.TileEntityMidChemicalTank;
import com.jerry.meklm.common.tile.machine.TileEntityLargeChemicalInfuser;
import com.jerry.meklm.common.tile.machine.TileEntityLargeElectrolyticSeparator;
import com.jerry.meklm.common.tile.machine.TileEntityLargeRotaryCondensentrator;
import com.jerry.meklm.common.tile.machine.TileEntityLargeSolarNeutronActivator;

import com.jerry.mekmm.Mekmm;

import mekanism.api.tier.ITier;
import mekanism.common.attachments.containers.ContainerType;
import mekanism.common.attachments.containers.chemical.ChemicalTanksBuilder;
import mekanism.common.attachments.containers.fluid.FluidTanksBuilder;
import mekanism.common.attachments.containers.item.ItemSlotsBuilder;
import mekanism.common.block.attribute.AttributeTier;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.content.blocktype.BlockType;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.item.block.ItemBlockTooltip;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.lookup.cache.InputRecipeCache;
import mekanism.common.recipe.lookup.cache.RotaryInputRecipeCache;
import mekanism.common.registration.impl.BlockDeferredRegister;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registries.MekanismDataComponents;
import mekanism.common.resource.BlockResourceInfo;
import mekanism.common.tile.TileEntityChemicalTank;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class LargeMachineBlocks {

    public static final BlockDeferredRegister LM_BLOCKS = new BlockDeferredRegister(Mekmm.MOD_ID);

    public static final BlockRegistryObject<BlockTileModel<TileEntityMidChemicalTank, Machine<TileEntityMidChemicalTank>>, ItemBlockMidChemicalTank> BASIC_MID_CHEMICAL_TANK = registerMidChemicalTank(LargeMachineBlockTypes.BASIC_MID_CHEMICAL_TANK);
    public static final BlockRegistryObject<BlockTileModel<TileEntityMidChemicalTank, Machine<TileEntityMidChemicalTank>>, ItemBlockMidChemicalTank> ADVANCED_MID_CHEMICAL_TANK = registerMidChemicalTank(LargeMachineBlockTypes.ADVANCED_MID_CHEMICAL_TANK);
    public static final BlockRegistryObject<BlockTileModel<TileEntityMidChemicalTank, Machine<TileEntityMidChemicalTank>>, ItemBlockMidChemicalTank> ELITE_MID_CHEMICAL_TANK = registerMidChemicalTank(LargeMachineBlockTypes.ELITE_MID_CHEMICAL_TANK);
    public static final BlockRegistryObject<BlockTileModel<TileEntityMidChemicalTank, Machine<TileEntityMidChemicalTank>>, ItemBlockMidChemicalTank> ULTIMATE_MID_CHEMICAL_TANK = registerMidChemicalTank(LargeMachineBlockTypes.ULTIMATE_MID_CHEMICAL_TANK);

    public static final BlockRegistryObject<BlockTileModel<TileEntityMaxChemicalTank, Machine<TileEntityMaxChemicalTank>>, ItemBlockMaxChemicalTank> BASIC_MAX_CHEMICAL_TANK = registerMaxChemicalTank(LargeMachineBlockTypes.BASIC_MAX_CHEMICAL_TANK);
    public static final BlockRegistryObject<BlockTileModel<TileEntityMaxChemicalTank, Machine<TileEntityMaxChemicalTank>>, ItemBlockMaxChemicalTank> ADVANCED_MAX_CHEMICAL_TANK = registerMaxChemicalTank(LargeMachineBlockTypes.ADVANCED_MAX_CHEMICAL_TANK);
    public static final BlockRegistryObject<BlockTileModel<TileEntityMaxChemicalTank, Machine<TileEntityMaxChemicalTank>>, ItemBlockMaxChemicalTank> ELITE_MAX_CHEMICAL_TANK = registerMaxChemicalTank(LargeMachineBlockTypes.ELITE_MAX_CHEMICAL_TANK);
    public static final BlockRegistryObject<BlockTileModel<TileEntityMaxChemicalTank, Machine<TileEntityMaxChemicalTank>>, ItemBlockMaxChemicalTank> ULTIMATE_MAX_CHEMICAL_TANK = registerMaxChemicalTank(LargeMachineBlockTypes.ULTIMATE_MAX_CHEMICAL_TANK);

    public static final BlockRegistryObject<BlockTileModel<TileEntityLargeRotaryCondensentrator, Machine<TileEntityLargeRotaryCondensentrator>>, ItemBlockTooltip<BlockTileModel<TileEntityLargeRotaryCondensentrator, Machine<TileEntityLargeRotaryCondensentrator>>>> LARGE_ROTARY_CONDENSENTRATOR = LM_BLOCKS.register("large_rotary_condensentrator", () -> new BlockTileModel<>(LargeMachineBlockTypes.LARGE_ROTARY_CONDENSENTRATOR,
            properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor())),
            (block, properties) -> new ItemBlockTooltip<>(block, true, properties
                    .component(MekanismDataComponents.ROTARY_MODE, false)))
            .forItemHolder(holder -> holder
                    .addAttachmentOnlyContainers(ContainerType.FLUID, () -> FluidTanksBuilder.builder()
                            .addBasic(TileEntityLargeRotaryCondensentrator.CAPACITY, MekanismRecipeType.ROTARY, RotaryInputRecipeCache::containsInput)
                            .build())
                    .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> ChemicalTanksBuilder.builder()
                            .addBasic(TileEntityLargeRotaryCondensentrator.CAPACITY, MekanismRecipeType.ROTARY, RotaryInputRecipeCache::containsInput)
                            .build())
                    .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                            .addChemicalRotaryDrainSlot(0)
                            .addChemicalRotaryFillSlot(0)
                            .addFluidRotarySlot(0)
                            .addOutput()
                            .addEnergy()
                            .build()));

    public static final BlockRegistryObject<BlockTileModel<TileEntityLargeChemicalInfuser, Machine<TileEntityLargeChemicalInfuser>>, ItemBlockTooltip<BlockTileModel<TileEntityLargeChemicalInfuser, Machine<TileEntityLargeChemicalInfuser>>>> LARGE_CHEMICAL_INFUSER = LM_BLOCKS.register("large_chemical_infuser", () -> new BlockTileModel<>(LargeMachineBlockTypes.LARGE_CHEMICAL_INFUSER, properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor())),
            (block, properties) -> new ItemBlockTooltip<>(block, true, properties))
            .forItemHolder(holder -> holder
                    .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> ChemicalTanksBuilder.builder()
                            .addBasic(TileEntityLargeChemicalInfuser.MAX_GAS, MekanismRecipeType.CHEMICAL_INFUSING, InputRecipeCache.EitherSideChemical::containsInput)
                            .addBasic(TileEntityLargeChemicalInfuser.MAX_GAS, MekanismRecipeType.CHEMICAL_INFUSING, InputRecipeCache.EitherSideChemical::containsInput)
                            .addBasic(2 * TileEntityLargeChemicalInfuser.MAX_GAS)
                            .build())
                    .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                            .addChemicalFillSlot(0)
                            .addChemicalFillSlot(1)
                            .addChemicalDrainSlot(2)
                            .addEnergy()
                            .build()));

    public static final BlockRegistryObject<BlockTileModel<TileEntityLargeElectrolyticSeparator, Machine<TileEntityLargeElectrolyticSeparator>>, ItemBlockTooltip<BlockTileModel<TileEntityLargeElectrolyticSeparator, Machine<TileEntityLargeElectrolyticSeparator>>>> LARGE_ELECTROLYTIC_SEPARATOR = LM_BLOCKS.register("large_electrolytic_separator", () -> new BlockTileModel<>(LargeMachineBlockTypes.LARGE_ELECTROLYTIC_SEPARATOR, properties -> properties.mapColor(BlockResourceInfo.STEEL.getMapColor())),
            (block, properties) -> new ItemBlockTooltip<>(block, true, properties
                    .component(MekanismDataComponents.DUMP_MODE, TileEntityChemicalTank.GasMode.IDLE)
                    .component(MekanismDataComponents.SECONDARY_DUMP_MODE, TileEntityChemicalTank.GasMode.IDLE)))
            .forItemHolder(holder -> holder
                    .addAttachmentOnlyContainers(ContainerType.FLUID, () -> FluidTanksBuilder.builder()
                            .addBasic(TileEntityLargeElectrolyticSeparator.MAX_FLUID, MekanismRecipeType.SEPARATING, InputRecipeCache.SingleFluid::containsInput)
                            .build())
                    .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> ChemicalTanksBuilder.builder()
                            .addBasic(TileEntityLargeElectrolyticSeparator.MAX_GAS)
                            .addBasic(TileEntityLargeElectrolyticSeparator.MAX_GAS)
                            .build())
                    .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                            .addFluidFillSlot(0)
                            .addChemicalDrainSlot(0)
                            .addChemicalDrainSlot(1)
                            .addEnergy()
                            .build()));

    public static final BlockRegistryObject<BlockTileModel<TileEntityLargeSolarNeutronActivator, Machine<TileEntityLargeSolarNeutronActivator>>, ItemBlockTooltip<BlockTileModel<TileEntityLargeSolarNeutronActivator, Machine<TileEntityLargeSolarNeutronActivator>>>> LARGE_SOLAR_NEUTRON_ACTIVATOR = LM_BLOCKS.register("large_solar_neutron_activator", () -> new BlockTileModel<>(LargeMachineBlockTypes.LARGE_SOLAR_NEUTRON_ACTIVATOR, properties -> properties.mapColor(MapColor.COLOR_BLUE)),
            (block, properties) -> new ItemBlockTooltip<>(block, true, properties))
            .forItemHolder(holder -> holder
                    .addAttachmentOnlyContainers(ContainerType.CHEMICAL, () -> ChemicalTanksBuilder.builder()
                            .addBasic(TileEntityLargeSolarNeutronActivator.MAX_GAS, MekanismRecipeType.ACTIVATING, InputRecipeCache.SingleChemical::containsInput)
                            .addBasic(TileEntityLargeSolarNeutronActivator.MAX_GAS)
                            .build())
                    .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                            .addChemicalFillSlot(0)
                            .addChemicalDrainSlot(1)
                            .build()));

    private static BlockRegistryObject<BlockTileModel<TileEntityMidChemicalTank, Machine<TileEntityMidChemicalTank>>, ItemBlockMidChemicalTank> registerMidChemicalTank(
                                                                                                                                                                        Machine<TileEntityMidChemicalTank> type) {
        return registerTieredBlock(type, "_mid_chemical_tank", color -> new BlockTileModel<>(type, properties -> properties.mapColor(color)), ItemBlockMidChemicalTank::new)
                .forItemHolder(holder -> holder
                        .addAttachedContainerCapabilities(ContainerType.CHEMICAL, () -> ChemicalTanksBuilder.builder()
                                .addTank(ComponentBackedLargeChemicalTankTank::create).build())
                        .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                                .addChemicalDrainSlot(0)
                                .addChemicalFillSlot(0)
                                .build()));
    }

    private static BlockRegistryObject<BlockTileModel<TileEntityMaxChemicalTank, Machine<TileEntityMaxChemicalTank>>, ItemBlockMaxChemicalTank> registerMaxChemicalTank(
                                                                                                                                                                        Machine<TileEntityMaxChemicalTank> type) {
        return registerTieredBlock(type, "_max_chemical_tank", color -> new BlockTileModel<>(type, properties -> properties.mapColor(color)), ItemBlockMaxChemicalTank::new)
                .forItemHolder(holder -> holder
                        .addAttachedContainerCapabilities(ContainerType.CHEMICAL, () -> ChemicalTanksBuilder.builder()
                                .addTank(ComponentBackedLargeChemicalTankTank::create).build())
                        .addAttachmentOnlyContainers(ContainerType.ITEM, () -> ItemSlotsBuilder.builder()
                                .addChemicalDrainSlot(0)
                                .addChemicalFillSlot(0)
                                .build()));
    }

    private static <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerTieredBlock(BlockType type, String suffix,
                                                                                                                      Function<MapColor, ? extends BLOCK> blockSupplier, BiFunction<BLOCK, Item.Properties, ITEM> itemCreator) {
        ITier tier = type.get(AttributeTier.class).tier();
        return registerTieredBlock(tier, suffix, () -> blockSupplier.apply(tier.getBaseTier().getMapColor()), itemCreator);
    }

    private static <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> registerTieredBlock(ITier tier, String suffix,
                                                                                                                      Supplier<? extends BLOCK> blockSupplier, BiFunction<BLOCK, Item.Properties, ITEM> itemCreator) {
        return LM_BLOCKS.register(tier.getBaseTier().getLowerName() + suffix, blockSupplier, itemCreator);
    }

    private LargeMachineBlocks() {}
}
